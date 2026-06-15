-- Active: 1751472576514@@127.0.0.1@5432@steam_clone_db@public

CREATE SCHEMA IF NOT EXISTS public;

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 0. Táº¡o database (náº¿u chÆ°a cÃ³)
-- Cháº¡y ngoÃ i psql, khÃ´ng cáº§n trong script nÃ y náº¿u báº¡n Ä‘Ã£ create sáºµn
-- CREATE DATABASE steam_clone_db;
-- \c steam_clone_db

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 1. Roles
CREATE TABLE public."Roles" (
    "RoleID" BIGSERIAL PRIMARY KEY,
    "RoleName" VARCHAR(50) NOT NULL
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 2. User
CREATE TABLE public."User" (
    "UserID" BIGSERIAL PRIMARY KEY,
    "RoleID" BIGINT NOT NULL REFERENCES public."Roles" ("RoleID"),
    "Email" VARCHAR(100) UNIQUE NOT NULL,
    "Username" VARCHAR(50) UNIQUE NOT NULL,
    "Password" VARCHAR(255) NOT NULL,
    "AvatarURL" TEXT,
    "CreatedAt" DATE,
    "WalletBalance" DECIMAL(10, 2) DEFAULT 0.00,
    "Country" VARCHAR(50),
    "DoB" DATE,
    "Gender" CHAR(1),
    "ProfileName" VARCHAR(50),
    "ThemeName" VARCHAR(50),
    "BackgroundName" VARCHAR(50),
    "Summary" TEXT,
    "BanStatus" BOOLEAN DEFAULT FALSE,
    "NewEmailAddress" VARCHAR(100),
    "EmailChangeToken" VARCHAR(10),
    "EmailChangeTokenExpiry" TIMESTAMP
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 2b. PasswordResetToken
CREATE TABLE public."PasswordResetToken" (
    "TokenID" BIGSERIAL PRIMARY KEY,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "OTP" VARCHAR(10) NOT NULL,
    "ExpiryTime" TIMESTAMP NOT NULL,
    "CreateAt" TIMESTAMP DEFAULT NOW()
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 3. Publisher
CREATE TABLE public."Publisher" (
    "PublisherID" BIGINT PRIMARY KEY REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "PublisherName" VARCHAR(100) NOT NULL,
    "CardNumber" VARCHAR(20),
    "Address" VARCHAR(255),
    "Country" VARCHAR(255),
    "ImageUrl" VARCHAR(255),
    "LegalName" VARCHAR(255),
    "SocialNumber" VARCHAR(12),
    "Suspend" INTEGER
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 4. Tags
CREATE TABLE public."Tags" (
    "TagID" BIGSERIAL PRIMARY KEY,
    "TagName" VARCHAR(50) UNIQUE NOT NULL
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 5. Game
CREATE TABLE public."Game" (
    "GameID" BIGSERIAL PRIMARY KEY,
    "PublisherID" BIGINT REFERENCES public."Publisher" ("PublisherID"),
    "Name" VARCHAR(100) NOT NULL,
    "ReleaseDate" DATE,
    "State" BOOLEAN,
    "Price" DECIMAL(10, 2),
    "ShortDescription" TEXT,
    "FullDescription" TEXT,
    "TotalPurchased" INTEGER DEFAULT 0,
    "OS" VARCHAR(50),
    "Storage" VARCHAR(50),
    "Processor" VARCHAR(50),
    "Memory" VARCHAR(50),
    "AdditionalNotes" TEXT,
    "Graphics" VARCHAR(50),
    "GameUrl" TEXT,
    "IconUrl" VARCHAR(255),
    "UpdateLog" TEXT
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 6. GameTags
CREATE TABLE public."GameTags" (
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    "TagID" BIGINT NOT NULL REFERENCES public."Tags" ("TagID") ON DELETE CASCADE,
    PRIMARY KEY ("GameID", "TagID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 7. Library
CREATE TABLE public."Library" (
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "DateAdded" TIMESTAMP,
    "PlaytimeInMillis" BIGINT DEFAULT 0 NOT NULL,
    "LastTimePlayed" TIMESTAMP,
    PRIMARY KEY ("GameID", "UserID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 8. Transaction
CREATE TABLE public."Transaction" (
    "TransactionID" BIGSERIAL PRIMARY KEY,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "TotalAmount" DECIMAL(10, 2),
    "CreatedAt" DATE,
    "Type" VARCHAR(255)
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 9. TransactionDetail
CREATE TABLE public."TransactionDetail" (
    "TransactionID" BIGINT NOT NULL REFERENCES public."Transaction" ("TransactionID") ON DELETE CASCADE,
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID"),
    "Price" DECIMAL(10, 2),
    PRIMARY KEY ("TransactionID", "GameID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 10. Review
CREATE TABLE public."Review" (
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "ReviewContent" TEXT,
    "IsRecommended" BOOLEAN,
    "TimeCreated" DATE,
    PRIMARY KEY ("GameID", "UserID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 11. ReviewHelpful
CREATE TABLE public."ReviewHelpful" (
    "HelpfulUserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "ReviewGameID" BIGINT NOT NULL,
    "ReviewUserID" BIGINT NOT NULL,
    PRIMARY KEY (
        "HelpfulUserID",
        "ReviewGameID",
        "ReviewUserID"
    ),
    FOREIGN KEY (
        "ReviewGameID",
        "ReviewUserID"
    ) REFERENCES public."Review" ("GameID", "UserID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 12. ReviewNotHelpful
CREATE TABLE public."ReviewNotHelpful" (
    "NotHelpfulUserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "ReviewGameID" BIGINT NOT NULL,
    "ReviewUserID" BIGINT NOT NULL,
    PRIMARY KEY (
        "NotHelpfulUserID",
        "ReviewGameID",
        "ReviewUserID"
    ),
    FOREIGN KEY (
        "ReviewGameID",
        "ReviewUserID"
    ) REFERENCES public."Review" ("GameID", "UserID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 13. Request
CREATE TABLE public."Request" (
    "RequestID" BIGSERIAL PRIMARY KEY,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "RequestType" VARCHAR(50),
    "TimeCreated" DATE,
    "UpdatedTime" DATE,
    "Status" INTEGER
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 14. PublisherApplyRequest
CREATE TABLE public."PublisherApplyRequest" (
    "RequestID" BIGINT PRIMARY KEY REFERENCES public."Request" ("RequestID") ON DELETE CASCADE,
    "PublisherName" VARCHAR(100),
    "CardNumber" VARCHAR(20),
    "Address" VARCHAR(255),
    "Country" VARCHAR(255),
    "ImageUrl" VARCHAR(255),
    "LegalName" VARCHAR(255),
    "SocialNumber" VARCHAR(12)
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 15. AddingGameRequest
CREATE TABLE public."AddingGameRequest" (
    "RequestID" BIGINT PRIMARY KEY REFERENCES public."Request" ("RequestID") ON DELETE CASCADE,
    "GameId" BIGINT,
    "GameName" VARCHAR(100),
    "ReleaseDate" DATE,
    "Price" DECIMAL(10, 2),
    "ShortDescription" TEXT,
    "FullDescription" TEXT,
    "OS" VARCHAR(50),
    "Storage" VARCHAR(50),
    "Processor" VARCHAR(50),
    "Memory" VARCHAR(50),
    "AdditionalNotes" TEXT,
    "Graphics" VARCHAR(50),
    "GameUrl" VARCHAR(255),
    "IconUrl" VARCHAR(255),
    "DeclineMessage" TEXT,
    "UpdateLog" TEXT
);

CREATE TABLE public."AddingGameRequest_mediaUrls" (
    "RequestID" BIGINT NOT NULL REFERENCES public."AddingGameRequest" ("RequestID") ON DELETE CASCADE,
    "MediaUrls" VARCHAR(255)
);

CREATE TABLE public."AddingGameRequest_tags" (
    "RequestID" BIGINT NOT NULL REFERENCES public."AddingGameRequest" ("RequestID") ON DELETE CASCADE,
    "Tags" INT
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 16. Feedback
CREATE TABLE public."Feedback" (
    "RequestID" BIGINT PRIMARY KEY REFERENCES public."Request" ("RequestID") ON DELETE CASCADE,
    "Subject" VARCHAR(256),
    "Message" TEXT,
    "Response" TEXT
);

CREATE TABLE public."Feedback_mediaUrls" (
    "RequestID" BIGINT NOT NULL REFERENCES public."Feedback" ("RequestID") ON DELETE CASCADE,
    "MediaUrls" VARCHAR(255)
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 17. Media
CREATE TABLE public."Media" (
    "MediaID" BIGSERIAL PRIMARY KEY,
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    "Url" VARCHAR(255) NOT NULL,
    "Type" VARCHAR(20) NOT NULL
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 18. Cart
CREATE TABLE public."Cart" (
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    "DateAdded" DATE,
    PRIMARY KEY ("UserID", "GameID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 19. Notifications
CREATE TABLE public."Notifications" (
    "NotificationID" BIGSERIAL PRIMARY KEY,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "NotificationType" VARCHAR(100) NOT NULL,
    "NotificationContent" TEXT NOT NULL,
    "IsRead" BOOLEAN
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 20. Friendships
CREATE TABLE public."Friendships" (
    "User1ID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "User2ID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "CreatedAt" DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY ("User1ID", "User2ID"),
    CHECK ("User1ID" < "User2ID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 21. Conversations
CREATE TABLE public."Conversations" (
    "ConversationID" BIGSERIAL PRIMARY KEY,
    "UserID1" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "UserID2" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "CreatedAt" DATE,
    CONSTRAINT UQ_UserPair UNIQUE ("UserID1", "UserID2"),
    CONSTRAINT CK_UserOrder CHECK ("UserID1" < "UserID2")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 22. Messages
CREATE TABLE public."Messages" (
    "MessageID" BIGSERIAL PRIMARY KEY,
    "ConversationID" BIGINT NOT NULL REFERENCES public."Conversations" ("ConversationID") ON DELETE CASCADE,
    "SenderID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "MessageContent" TEXT NOT NULL,
    "SentAt" TIMESTAMP DEFAULT NOW()
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 23. FriendRequests
CREATE TABLE public."FriendRequests" (
    "RequestID" BIGSERIAL PRIMARY KEY,
    "SenderID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "ReceiverID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "CreatedAt" DATE DEFAULT CURRENT_DATE,
    UNIQUE ("SenderID", "ReceiverID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 24. Blocks
CREATE TABLE public."Blocks" (
    "BlockerID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "BlockedID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "CreatedAt" DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY ("BlockerID", "BlockedID")
);
-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 25. Group Chat
CREATE TABLE public."GroupChat" (
    "GroupID" BIGSERIAL PRIMARY KEY,
    "GroupName" VARCHAR(100) NOT NULL,
    "OwnerID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE SET NULL,
    "CreatedAt" TIMESTAMP DEFAULT NOW()
);
-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 26. Group Chat Member
CREATE TABLE public."GroupChatMember" (
    "GroupID" BIGINT NOT NULL REFERENCES public."GroupChat" ("GroupID") ON DELETE CASCADE,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "IsAdmin" BOOLEAN DEFAULT FALSE,
    "JoinedAt" TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY ("GroupID", "UserID")
);
-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 27. Group Message
CREATE TABLE public."GroupMessage" (
    "MessageID" BIGSERIAL PRIMARY KEY,
    "GroupID" BIGINT NOT NULL REFERENCES public."GroupChat" ("GroupID") ON DELETE CASCADE,
    "SenderID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "Message" TEXT NOT NULL,
    "SentAt" TIMESTAMP DEFAULT NOW()
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 28. Family
CREATE TABLE public."Family" (
    "FamilyID" BIGSERIAL PRIMARY KEY,
    "OwnerID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "CreatedAt" DATE DEFAULT CURRENT_DATE,
    "ExpDate" TIMESTAMP NOT NULL
);
-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 29. Family Member
CREATE TABLE public."FamilyMember" (
    "FamilyID" BIGINT NOT NULL REFERENCES public."GroupChat" ("GroupID") ON DELETE CASCADE,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "IsOwner" BOOLEAN DEFAULT FALSE,
    "JoinedAt" DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY ("FamilyID", "UserID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 30. Family Invitation
CREATE TABLE public."FamilyInvitation" (
    "InviteID" BIGSERIAL PRIMARY KEY,
    "FamilyID" BIGINT NOT NULL REFERENCES public."Family" ("FamilyID"),
    "InvitorID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "ReceiverID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "CreatedAt" DATE DEFAULT CURRENT_DATE,
    "ExpiresAt"  DATE NOT NULL,
    UNIQUE ("InvitorID", "ReceiverID")
);

-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- 31. Library
CREATE TABLE public."FamilyLibrary" (
    "FamilyID" BIGINT NOT NULL REFERENCES public."Family" ("FamilyID"),
    "GameID" BIGINT NOT NULL REFERENCES public."Game" ("GameID") ON DELETE CASCADE,
    PRIMARY KEY ("GameID", "FamilyID")
);

-- 32. Discussion Thread
CREATE TABLE public."DiscussionThread" (
    "threadId" BIGSERIAL PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL,
    "content" TEXT,
    "createdAt" TIMESTAMP,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "GameID" BIGINT REFERENCES public."Game" ("GameID")
);

-- 33. Discussion Comment
CREATE TABLE public."DiscussionComment" (
    "commentId" BIGSERIAL PRIMARY KEY,
    "content" TEXT NOT NULL,
    "createdAt" TIMESTAMP,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID"),
    "ThreadID" BIGINT NOT NULL REFERENCES public."DiscussionThread" ("threadId") ON DELETE CASCADE
);

-- 34. Subscription Plan
CREATE TABLE public."SubscriptionPlan" (
    "PlanID" BIGSERIAL PRIMARY KEY,
    "FamilyID" BIGINT NOT NULL REFERENCES public."Family" ("FamilyID") ON DELETE CASCADE,
    "PlanName" VARCHAR(50) NOT NULL,
    "DurationInDays" INTEGER NOT NULL,
    "Price" DECIMAL(10, 2) NOT NULL,
    "StartAt" TIMESTAMP NOT NULL,
    "EndAt" TIMESTAMP NOT NULL,
    "Note" TEXT,
    "CreatedAt" DATE DEFAULT CURRENT_DATE
);

-- 35. Email Verification Token
CREATE TABLE public."EmailVerificationToken" (
    "id" BIGSERIAL PRIMARY KEY,
    "email" VARCHAR(255) NOT NULL,
    "otp" VARCHAR(10) NOT NULL,
    "expiryTime" TIMESTAMP NOT NULL,
    "createdAt" TIMESTAMP DEFAULT NOW()
);

-- 36. News
CREATE TABLE public."News" (
    "NewsID" BIGSERIAL PRIMARY KEY,
    "Title" TEXT NOT NULL,
    "Summary" TEXT NOT NULL,
    "Markdown" TEXT,
    "CreatedAt" DATE,
    "Thumbnail" VARCHAR(255),
    "GameID" BIGINT REFERENCES public."Game" ("GameID")
);

-- 37. SessionLog
CREATE TABLE public."SessionLog" (
    "SessionID" BIGSERIAL PRIMARY KEY,
    "UserID" BIGINT NOT NULL REFERENCES public."User" ("UserID") ON DELETE CASCADE,
    "LoginTime" TIMESTAMP NOT NULL
);
-- â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”â€”
-- Seed initial data
INSERT INTO
    public."Roles" ("RoleName")
VALUES ('Standard'),
    ('Publisher'),
    ('Admin');

