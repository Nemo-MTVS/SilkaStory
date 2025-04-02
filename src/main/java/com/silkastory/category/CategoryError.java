package com.silkastory.category;

/**
 * 카테고리 관련 오류 메시지를 정의한 열거형
 */
public enum CategoryError {
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    CATEGORY_NOT_BELONG_TO_USER("해당 카테고리에 접근 권한이 없습니다."),
    CATEGORY_HAS_CHILDREN("하위 카테고리가 있어 삭제할 수 없습니다."),
    CATEGORY_IS_USED("이미 사용 중인 카테고리는 삭제할 수 없습니다.");
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
