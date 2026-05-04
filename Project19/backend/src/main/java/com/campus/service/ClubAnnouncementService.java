package com.campus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.ClubAnnouncement;

import java.util.List;

public interface ClubAnnouncementService extends IService<ClubAnnouncement> {

    ClubAnnouncement getByIdWithDetail(Long id);

    List<ClubAnnouncement> listByClubId(Long clubId);

    Page<ClubAnnouncement> pageByClubId(Page<ClubAnnouncement> page, Long clubId, Integer type);

    boolean createAnnouncement(ClubAnnouncement announcement, Long userId);

    boolean updateAnnouncement(ClubAnnouncement announcement, Long userId);

    boolean deleteAnnouncement(Long id, Long userId);

    boolean toggleTop(Long id, Long userId);

    void incrementReadCount(Long id);
}
