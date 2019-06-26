package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 * @date 2018/6/8
 */
public enum Status {
    /*
    状态，0是待完善 1是待审核 2是审核通过  3是审核失败 4是已提交 5是运行 6是失败 7是取消 8是完成
     */
    UN_FIX(0), UN_AUDIT(1), AUDIT_PASS(2), AUDIT_FAIL(3), COMMIT(4), RUNNING(5), FAILED(6), CANCELED(7), FINISHED(8);
    private int status;

    Status(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static Status fromStatus(int status) {
        for (Status b : values()) {
            if (b != null && b.getStatus() == status) {
                return b;
            }
        }
        return null;
    }
}
