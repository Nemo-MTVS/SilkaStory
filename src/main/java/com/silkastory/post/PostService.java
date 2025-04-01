package com.silkastory.post;

import java.sql.SQLException;

public class PostService {
    //비즈니스 로직을 처리하는 계층 클래스
    private PostDAO postDAO = new PostDAO();


    public boolean addPost(String userId, String title, String content, int category_id) throws SQLException  {
        try{

            //rows를
            Post post = new Post(title, content,userId, category_id);
            int rows = postDAO.createPost(post);
            return rows > 0;  // 삽입 성공 여부 반환

        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
//        return false;
    }
}