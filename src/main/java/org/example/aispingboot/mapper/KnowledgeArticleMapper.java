package org.example.aispingboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.aispingboot.entity.KnowledgeArticle;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {

    @Update("UPDATE knowledge_article SET read_count = read_count + 1 WHERE id = #{id} AND status = 1")
    int incrementReadCount(@Param("id") String id);
}
