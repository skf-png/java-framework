package framework.admin.service.map.domain.DTO;

import lombok.Data;

@Data
public class SuggestSearchDTO {

    /**
     * 搜索的关键字
     */
    private String keyword;

    /**
     * 城市id
     */
    private String id;

    /**
     * 页码
     */
    private Integer pageIndex;

    /**
     * 每页的数量
     */
    private Integer pageSize;
}
