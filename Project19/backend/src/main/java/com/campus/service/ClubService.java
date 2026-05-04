package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;

import java.util.List;

public interface ClubService extends IService<Club> {

    Page<Club> pageClubs(Page<Club> page, String name, Long categoryId, Integer status);

    Club getClubDetail(Long id);

    boolean createClub(Club club, Long userId);

    boolean auditClub(Long id, Integer status, String auditRemark);

    boolean applyJoin(Long clubId, Long userId, String remark);

    boolean auditJoin(Long memberId, Integer status, String auditRemark);

    boolean quitClub(Long clubId, Long userId);

    List<ClubMember> getClubMembers(Long clubId);

    List<ClubMember> getMyClubs(Long userId);

    List<ClubMember> getMyApplyClubs(Long userId);

    List<ClubMember> getPendingApprovals(Long clubId);
}
