package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.ResultCode;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import com.campus.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements ClubService {

    private final ClubMemberMapper clubMemberMapper;

    @Override
    public Page<Club> pageClubs(Page<Club> page, String name, Long categoryId, Integer status) {
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Club::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Club::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Club::getStatus, status);
        }
        wrapper.orderByDesc(Club::getCreateTime);
        Page<Club> result = page(page, wrapper);
        
        result.getRecords().forEach(club -> {
            Club detail = baseMapper.getByIdWithDetail(club.getId());
            if (detail != null) {
                club.setCategoryName(detail.getCategoryName());
                club.setPresidentName(detail.getPresidentName());
            }
        });
        
        return result;
    }

    @Override
    public Club getClubDetail(Long id) {
        return baseMapper.getByIdWithDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createClub(Club club, Long userId) {
        Club existing = getOne(new LambdaQueryWrapper<Club>().eq(Club::getName, club.getName()));
        if (existing != null) {
            throw new RuntimeException(ResultCode.CLUB_NAME_EXIST.getMessage());
        }
        
        club.setPresidentId(userId);
        club.setCurrentMembers(0);
        club.setStatus(Club.STATUS_PENDING);
        save(club);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditClub(Long id, Integer status, String auditRemark) {
        Club club = getById(id);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        club.setStatus(status);
        club.setAuditRemark(auditRemark);
        
        if (status == Club.STATUS_APPROVED) {
            ClubMember member = new ClubMember();
            member.setClubId(id);
            member.setUserId(club.getPresidentId());
            member.setRole(ClubMember.ROLE_PRESIDENT);
            member.setStatus(ClubMember.STATUS_APPROVED);
            member.setJoinTime(LocalDateTime.now());
            clubMemberMapper.insert(member);
            
            club.setCurrentMembers(1);
        }
        
        return updateById(club);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyJoin(Long clubId, Long userId, String remark) {
        Club club = getById(clubId);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        if (club.getStatus() != Club.STATUS_APPROVED) {
            throw new RuntimeException("社团未审核通过，无法加入");
        }
        
        ClubMember existing = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        
        if (existing != null) {
            if (existing.getStatus() == ClubMember.STATUS_PENDING) {
                throw new RuntimeException(ResultCode.ALREADY_APPLIED.getMessage());
            }
            if (existing.getStatus() == ClubMember.STATUS_APPROVED) {
                throw new RuntimeException(ResultCode.ALREADY_MEMBER.getMessage());
            }
        }
        
        if (club.getCurrentMembers() >= club.getMaxMembers()) {
            throw new RuntimeException(ResultCode.CLUB_FULL.getMessage());
        }
        
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole(ClubMember.ROLE_MEMBER);
        member.setStatus(ClubMember.STATUS_PENDING);
        member.setApplyRemark(remark);
        
        return clubMemberMapper.insert(member) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditJoin(Long memberId, Integer status, String auditRemark) {
        ClubMember member = clubMemberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("申请记录不存在");
        }
        
        member.setStatus(status);
        member.setAuditRemark(auditRemark);
        
        if (status == ClubMember.STATUS_APPROVED) {
            member.setJoinTime(LocalDateTime.now());
            
            Club club = getById(member.getClubId());
            if (club != null) {
                club.setCurrentMembers(club.getCurrentMembers() + 1);
                updateById(club);
            }
        }
        
        return clubMemberMapper.updateById(member) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitClub(Long clubId, Long userId) {
        ClubMember member = clubMemberMapper.selectOne(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
        );
        
        if (member == null || member.getStatus() != ClubMember.STATUS_APPROVED) {
            throw new RuntimeException("不是该社团成员");
        }
        
        if (member.getRole() == ClubMember.ROLE_PRESIDENT) {
            throw new RuntimeException("社长不能退出社团，请先转让社长职位");
        }
        
        member.setStatus(ClubMember.STATUS_QUIT);
        clubMemberMapper.updateById(member);
        
        Club club = getById(clubId);
        if (club != null) {
            club.setCurrentMembers(Math.max(0, club.getCurrentMembers() - 1));
            updateById(club);
        }
        
        return true;
    }

    @Override
    public List<ClubMember> getClubMembers(Long clubId) {
        return clubMemberMapper.listByClubId(clubId);
    }

    @Override
    public List<ClubMember> getMyClubs(Long userId) {
        List<ClubMember> members = clubMemberMapper.listByUserId(userId);
        return members.stream()
            .filter(m -> m.getStatus() == ClubMember.STATUS_APPROVED)
            .collect(Collectors.toList());
    }

    @Override
    public List<ClubMember> getMyApplyClubs(Long userId) {
        List<ClubMember> members = clubMemberMapper.listByUserId(userId);
        return members.stream()
            .filter(m -> m.getStatus() == ClubMember.STATUS_PENDING)
            .collect(Collectors.toList());
    }

    @Override
    public List<ClubMember> getPendingApprovals(Long clubId) {
        return clubMemberMapper.selectList(
            new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getStatus, ClubMember.STATUS_PENDING)
        );
    }
}
