package com.example.moble_project.test.DTO;

public class PostGalleryDTO {

    // 예제: API가 아래와 같은 JSON 응답을 반환한다고 가정
    /*
    {
        "status": "success",
        "message": "Image uploaded successfully",
        "data": {
            "imageUrl": "https://example.com/path/to/uploaded/image.jpg"
        }
    }
    */

    private String status;
    private String message;
    private Data data;

    // Getter와 Setter는 필수입니다
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // 내부 클래스로 Data 클래스를 정의
    public static class Data {
        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
