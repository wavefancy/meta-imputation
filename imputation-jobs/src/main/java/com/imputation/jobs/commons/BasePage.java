package com.imputation.jobs.commons;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fanshupeng
 * @create 2022/8/25 10:37
 */
@Getter
@Setter
public class BasePage {
    protected long size;
    protected long current;
    public BasePage() {
        this.size = 10L;
        this.current = 1L;
    }
}
