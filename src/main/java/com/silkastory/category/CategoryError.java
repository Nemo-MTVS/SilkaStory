package com.silkastory.category;

public enum CategoryError {
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다"),
    CATEGORY_NOT_BELONG_TO_USER("카테고리가 사용자의 것이 아닙니다"),
    CATEGORY_HAS_CHILDREN("카테고리에 하위 카테고리가 존재합니다"),
    CATEGORY_IS_USED("카테고리가 사용중입니다"),;
    // CATEGORY_ALREADY_EXISTS("Category already exists"),
    // INVALID_CATEGORY_NAME("Invalid category name"),
    // CATEGORY_DELETION_FAILED("Category deletion failed"),
    // CATEGORY_UPDATE_FAILED("Category update failed");

    private final String message;

    CategoryError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
