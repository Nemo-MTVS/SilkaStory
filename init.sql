# -- bookmarks
# ALTER TABLE `bookmarks` DROP FOREIGN KEY `fk_bookmarks_user_id__users_id`;
# ALTER TABLE `bookmarks` DROP FOREIGN KEY `fk_bookmarks_post_id__posts_id`;
#
# -- categories
# ALTER TABLE `categories` DROP FOREIGN KEY `fk_categories_user_id__users_id`;
# ALTER TABLE `categories` DROP FOREIGN KEY `fk_categories_target_category_id__categories_id`;
#
# -- comments
# ALTER TABLE `comments` DROP FOREIGN KEY `fk_comments_post_id__posts_id`;
# ALTER TABLE `comments` DROP FOREIGN KEY `fk_comments_user_id__users_id`;
#
# -- notifications
# ALTER TABLE `notifications` DROP FOREIGN KEY `fk_notifications_user_id__users_id`;
#
# -- posts
# ALTER TABLE `posts` DROP FOREIGN KEY `fk_posts_user_id__users_id`;
# ALTER TABLE `posts` DROP FOREIGN KEY `fk_posts_category_id__categories_id`;
#
# -- replies
# ALTER TABLE `replies` DROP FOREIGN KEY `fk_replies_comment_id__comments_id`;
# ALTER TABLE `replies` DROP FOREIGN KEY `fk_replies_user_id__users_id`;
#
# -- subscriptions
# ALTER TABLE `subscriptions` DROP FOREIGN KEY `fk_subscriptions_user_id__users_id`;
# ALTER TABLE `subscriptions` DROP FOREIGN KEY `fk_subscriptions_target_id__users_id`;
#
# -- visitor
# ALTER TABLE `visitor` DROP FOREIGN KEY `fk_visitor_post_id__posts_id`;
# ALTER TABLE `visitor` DROP FOREIGN KEY `fk_visitor_user_id__users_id`;


DROP TABLE IF EXISTS `bookmarks`;
DROP TABLE IF EXISTS `visitor`;
DROP TABLE IF EXISTS `subscriptions`;
DROP TABLE IF EXISTS `replies`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `posts`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;


-- 1. users
CREATE TABLE `users` (
                         `id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                         `name` VARCHAR(100) NOT NULL COMMENT '이름',
                         `nickname` VARCHAR(100) NOT NULL COMMENT '닉네임',
#                          `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                          `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                          `deletedAt` DATETIME COMMENT '삭제일',
#                          `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
#                          UNIQUE KEY `uk_users_nickname` (`nickname`),
                         PRIMARY KEY (`id`)
) COMMENT = '회원';

-- 2. categories
CREATE TABLE `categories` (
                              `id` BIGINT NOT NULL COMMENT '카테고리 식별값',
                              `name` VARCHAR(100) NOT NULL COMMENT '카테고리 제목',
                              `depth` INTEGER NOT NULL COMMENT '카테고리 깊이',
#                               `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                               `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                               `deletedAt` DATETIME COMMENT '삭제일',
#                               `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                              `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                              `target_category_id` BIGINT NOT NULL COMMENT '참조 카테고리 식별값',
                              PRIMARY KEY (`id`)
#                               CONSTRAINT `fk_categories_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
#                               CONSTRAINT `fk_categories_target_category_id__categories_id` FOREIGN KEY (`target_category_id`) REFERENCES `categories` (`id`)
) COMMENT = '카테고리';

-- 3. posts
CREATE TABLE `posts` (
                         `id` BIGINT NOT NULL COMMENT '게시글 식별값',
                         `title` VARCHAR(255) NOT NULL COMMENT '제목',
                         `content` VARCHAR(4000) NOT NULL COMMENT '내용',
#                          `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                          `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                          `deletedAt` DATETIME COMMENT '삭제일',
#                          `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                         `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                         `category_id` BIGINT NOT NULL COMMENT '카테고리 식별값',
                         `is_public` BOOLEAN NOT NULL COMMENT '공개 여부',
                         PRIMARY KEY (`id`)
#                          CONSTRAINT `fk_posts_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
#                          CONSTRAINT `fk_posts_category_id__categories_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) COMMENT = '게시글';

-- 4. comments
CREATE TABLE `comments` (
                            `id` BIGINT NOT NULL COMMENT '댓글 식별값',
                            `content` LONGTEXT NOT NULL COMMENT '내용',
#                             `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                             `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                             `deletedAt` DATETIME COMMENT '삭제일',
#                             `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                            `post_id` BIGINT NOT NULL COMMENT '게시글 식별값',
                            `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                            PRIMARY KEY (`id`)
#                             CONSTRAINT `fk_comments_post_id__posts_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
#                             CONSTRAINT `fk_comments_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT = '댓글';

-- 5. replies
CREATE TABLE `replies` (
                           `id` BIGINT NOT NULL COMMENT '답글 식별값',
                           `content` LONGTEXT NOT NULL COMMENT '내용',
#                            `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                            `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                            `deletedAt` DATETIME COMMENT '삭제일',
#                            `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                           `comment_id` BIGINT NOT NULL COMMENT '댓글 식별값',
                           `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                           PRIMARY KEY (`id`)
#                            CONSTRAINT `fk_replies_comment_id__comments_id` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`),
#                            CONSTRAINT `fk_replies_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT = '답글';

-- 6. notifications
CREATE TABLE `notifications` (
                                 `id` BIGINT NOT NULL COMMENT '알림 식별값',
                                 `message` LONGTEXT NOT NULL COMMENT '내용',
                                 `state` BOOLEAN NOT NULL COMMENT '읽음 상태',
#                                  `createdAt` DATETIME NOT NULL COMMENT '생성일',
                                 `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                                 PRIMARY KEY (`id`)
#                                  CONSTRAINT `fk_notifications_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT = '알림';

-- 7. subscriptions
CREATE TABLE `subscriptions` (
                                 `id` BIGINT NOT NULL COMMENT '구독 식별값',
#                                  `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                                  `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                                  `deletedAt` DATETIME COMMENT '삭제일',
#                                      `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                                 `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값(누가)',
                                 `target_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값2(누구를)',
                                 `is_alram` BOOLEAN COMMENT '알림 여부',
                                 PRIMARY KEY (`id`)
#                                  CONSTRAINT `fk_subscriptions_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
#                                  CONSTRAINT `fk_subscriptions_target_id__users_id` FOREIGN KEY (`target_id`) REFERENCES `users` (`id`)
) COMMENT = '구독';

-- 8. visitor
CREATE TABLE `visitor` (
                           `id` BIGINT NOT NULL COMMENT '식별값',
#                            `createdAt` DATETIME NOT NULL COMMENT '생성일',
                           `post_id` BIGINT NOT NULL COMMENT '게시글 식별값',
                           `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                           PRIMARY KEY (`id`)
#                            UNIQUE KEY `uk_bookmarks_user_id_post_id` (`user_id`, `post_id`),
#                            CONSTRAINT `fk_visitor_post_id__posts_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
#                            CONSTRAINT `fk_visitor_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT = '방문자';

-- 9. bookmarks
CREATE TABLE `bookmarks` (
                             `id` BIGINT NOT NULL COMMENT '즐겨찾기 식별값',
                             `name` LONGTEXT NOT NULL COMMENT '이름',
#                              `createdAt` DATETIME NOT NULL COMMENT '생성일',
#                              `updatedAt` DATETIME NOT NULL COMMENT '수정일',
#                              `deletedAt` DATETIME COMMENT '삭제일',
#                              `isUsed` BOOLEAN NOT NULL COMMENT '삭제상태',
                             `user_id` VARCHAR(255) NOT NULL COMMENT '회원 식별 값',
                             `post_id` BIGINT NOT NULL COMMENT '게시글 식별값',
                             PRIMARY KEY (`id`)
#                             UNIQUE KEY `uk_bookmarks_user_id_post_id` (`user_id`, `post_id`),
#                              CONSTRAINT `fk_bookmarks_user_id__users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
#                              CONSTRAINT `fk_bookmarks_post_id__posts_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) COMMENT = '즐겨찾기';
