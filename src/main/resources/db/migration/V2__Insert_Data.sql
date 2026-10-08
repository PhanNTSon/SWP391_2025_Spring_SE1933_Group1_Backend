-- Active: 1751472576514@@127.0.0.1@5432@steam_clone_db@public
-- B?T Ð?U KH?I L?NH Ð? CHÈN D? LI?U M?U
DO $$
DECLARE
    -- Bi?n cho Roles
    role_publisher_id BIGINT;
    role_user_id BIGINT;
    role_admin_id BIGINT;

    -- Bi?n cho User/Publisher ID
    user_microsoft_id BIGINT;
    user_toby_fox_id BIGINT;
    user_kikiyama_id BIGINT;
    player_one_id BIGINT;
    user_valve_id BIGINT;
    user_cd_projekt_id BIGINT;
    user_mojang_id BIGINT;
    user_nintendo_id BIGINT;

    -- Bi?n cho Tag ID
    tag_id_rts INT; tag_id_strategy INT; tag_id_historical INT; tag_id_classic INT;
    tag_id_singleplayer INT; tag_id_multiplayer INT; tag_id_rpg INT; tag_id_indie INT;
    tag_id_story_rich INT; tag_id_great_soundtrack INT; tag_id_pixel_graphics INT;
    tag_id_choices_matter INT; tag_id_surreal INT; tag_id_horror INT;
    tag_id_exploration INT; tag_id_free_to_play INT; tag_id_adventure INT;
    tag_id_action INT; tag_id_open_world INT; tag_id_fantasy INT; tag_id_sci_fi INT;
    tag_id_survival INT; tag_id_crafting INT; tag_id_simulation INT;
    tag_id_platformer INT; tag_id_puzzle INT; tag_id_co_op INT; tag_id_fps INT;
    tag_id_anime INT; tag_id_racing INT; tag_id_casual INT;

    -- Bi?n cho Game ID
    game_id_aoe BIGINT; game_id_undertale BIGINT; game_id_yume_nikki BIGINT;
    game_id_half_life2 BIGINT; game_id_witcher3 BIGINT; game_id_minecraft BIGINT;
    game_id_mario_kart8 BIGINT; game_id_portal2 BIGINT; game_id_cyberpunk2077 BIGINT;
    game_id_stardew_valley BIGINT; game_id_hollow_knight BIGINT; game_id_among_us BIGINT;
    game_id_celeste BIGINT; game_id_outer_wilds BIGINT; game_id_deltarune BIGINT;
    
BEGIN
    --------------------------------------------------------------------------------
    -- LÔ 1: INSERT D? LI?U CO B?N (ROLES, USERS, PUBLISHERS, TAGS)
    --------------------------------------------------------------------------------
    RAISE NOTICE 'B?t d?u l?y ID c?a các Roles...';
    -- D? li?u Roles dã du?c chèn trong migrate.sql. ? dây ch? l?y ID.
    SELECT "RoleID" INTO role_user_id FROM public."Roles" WHERE "RoleName" = 'Standard';
    SELECT "RoleID" INTO role_publisher_id FROM public."Roles" WHERE "RoleName" = 'Publisher';
    SELECT "RoleID" INTO role_admin_id FROM public."Roles" WHERE "RoleName" = 'Admin';
    RAISE NOTICE 'Hoàn thành l?y ID c?a các Roles.';

    -- 2. Thêm Users v?i RoleID tuong ?ng
    RAISE NOTICE 'Ðang chèn Users...';
    INSERT INTO public."User" ("RoleID", "Email", "Username", "Password", "ProfileName", "Country") VALUES
    (role_publisher_id, 'microsoft@example.com', 'MicrosoftStudios', 'pass123', 'Microsoft Game Studios', 'USA'),
    (role_publisher_id, 'toby@example.com', 'tobyfox', 'pass123', 'Toby Fox', 'USA'),
    (role_publisher_id, 'kiki@example.com', 'kikiyama', 'pass123', 'Kikiyama', 'Japan'),
    (role_user_id, 'player1@example.com', 'PlayerOne', 'pass123', 'Player Uno', 'Canada'),
    (role_publisher_id, 'valve@example.com', 'Valve', 'pass123', 'Valve Corporation', 'USA'),
    (role_publisher_id, 'cdprojekt@example.com', 'CDProjektRed', 'pass123', 'CD Projekt Red', 'Poland'),
    (role_publisher_id, 'mojang@example.com', 'Mojang', 'pass123', 'Mojang Studios', 'Sweden'),
    (role_publisher_id, 'nintendo@example.com', 'Nintendo', 'pass123', 'Nintendo', 'Japan');

    -- Gán UserID vào các bi?n SAU KHI INSERT
    SELECT "UserID" INTO user_microsoft_id FROM public."User" WHERE "Username" = 'MicrosoftStudios';
    SELECT "UserID" INTO user_toby_fox_id FROM public."User" WHERE "Username" = 'tobyfox';
    SELECT "UserID" INTO user_kikiyama_id FROM public."User" WHERE "Username" = 'kikiyama';
    SELECT "UserID" INTO player_one_id FROM public."User" WHERE "Username" = 'PlayerOne';
    SELECT "UserID" INTO user_valve_id FROM public."User" WHERE "Username" = 'Valve';
    SELECT "UserID" INTO user_cd_projekt_id FROM public."User" WHERE "Username" = 'CDProjektRed';
    SELECT "UserID" INTO user_mojang_id FROM public."User" WHERE "Username" = 'Mojang';
    SELECT "UserID" INTO user_nintendo_id FROM public."User" WHERE "Username" = 'Nintendo';
    RAISE NOTICE 'Hoàn thành chèn Users.';

    -- 3. Thêm Publishers (PublisherID chính là UserID c?a User có vai trò Publisher)
    RAISE NOTICE 'Ðang chèn Publishers...';
    INSERT INTO public."Publisher" ("PublisherID", "PublisherName", "CardNumber") VALUES
    (user_microsoft_id, 'Microsoft Game Studios', '1111-2222-3333-4444'),
    (user_toby_fox_id, 'Toby Fox', '5555-6666-7777-8888'),
    (user_kikiyama_id, 'Kikiyama', '9999-0000-1111-2222'),
    (user_valve_id, 'Valve Corporation', '1234-5678-9012-3456'),
    (user_cd_projekt_id, 'CD Projekt Red', '9876-5432-1098-7654'),
    (user_mojang_id, 'Mojang Studios', '1122-3344-5566-7788'),
    (user_nintendo_id, 'Nintendo', '0000-0000-0000-0000');
    RAISE NOTICE 'Hoàn thành chèn Publishers.';

    -- 4. Thêm Tags
    RAISE NOTICE 'Ðang chèn Tags...';
    INSERT INTO public."Tags" ("TagName") VALUES
    ('RTS'), ('Strategy'), ('Historical'), ('Classic'), ('Singleplayer'),
    ('Multiplayer'), ('RPG'), ('Indie'), ('Story Rich'), ('Great Soundtrack'),
    ('Pixel Graphics'), ('Choices Matter'), ('Surreal'), ('Horror'),
    ('Exploration'), ('Free to Play'), ('Adventure'), ('Action'), ('Open World'),
    ('Fantasy'), ('Sci-fi'), ('Survival'), ('Crafting'), ('Simulation'),
    ('Platformer'), ('Puzzle'), ('Co-op'), ('FPS'), ('Anime'), ('Racing'),('Casual');
    RAISE NOTICE 'Hoàn thành chèn Tags.';

    --------------------------------------------------------------------------------
    -- LÔ 2: INSERT D? LI?U GAME (GAMES, GAMETAGS, MEDIA)
    --------------------------------------------------------------------------------
    RAISE NOTICE 'B?t d?u l?y ID c?a các Tags...';
    SELECT "TagID" INTO tag_id_rts FROM public."Tags" WHERE "TagName" = 'RTS';
    SELECT "TagID" INTO tag_id_strategy FROM public."Tags" WHERE "TagName" = 'Strategy';
    SELECT "TagID" INTO tag_id_historical FROM public."Tags" WHERE "TagName" = 'Historical';
    SELECT "TagID" INTO tag_id_classic FROM public."Tags" WHERE "TagName" = 'Classic';
    SELECT "TagID" INTO tag_id_singleplayer FROM public."Tags" WHERE "TagName" = 'Singleplayer';
    SELECT "TagID" INTO tag_id_multiplayer FROM public."Tags" WHERE "TagName" = 'Multiplayer';
    SELECT "TagID" INTO tag_id_rpg FROM public."Tags" WHERE "TagName" = 'RPG';
    SELECT "TagID" INTO tag_id_indie FROM public."Tags" WHERE "TagName" = 'Indie';
    SELECT "TagID" INTO tag_id_story_rich FROM public."Tags" WHERE "TagName" = 'Story Rich';
    SELECT "TagID" INTO tag_id_great_soundtrack FROM public."Tags" WHERE "TagName" = 'Great Soundtrack';
    SELECT "TagID" INTO tag_id_pixel_graphics FROM public."Tags" WHERE "TagName" = 'Pixel Graphics';
    SELECT "TagID" INTO tag_id_choices_matter FROM public."Tags" WHERE "TagName" = 'Choices Matter';
    SELECT "TagID" INTO tag_id_surreal FROM public."Tags" WHERE "TagName" = 'Surreal';
    SELECT "TagID" INTO tag_id_horror FROM public."Tags" WHERE "TagName" = 'Horror';
    SELECT "TagID" INTO tag_id_exploration FROM public."Tags" WHERE "TagName" = 'Exploration';
    SELECT "TagID" INTO tag_id_free_to_play FROM public."Tags" WHERE "TagName" = 'Free to Play';
    SELECT "TagID" INTO tag_id_adventure FROM public."Tags" WHERE "TagName" = 'Adventure';
    SELECT "TagID" INTO tag_id_action FROM public."Tags" WHERE "TagName" = 'Action';
    SELECT "TagID" INTO tag_id_open_world FROM public."Tags" WHERE "TagName" = 'Open World';
    SELECT "TagID" INTO tag_id_fantasy FROM public."Tags" WHERE "TagName" = 'Fantasy';
    SELECT "TagID" INTO tag_id_sci_fi FROM public."Tags" WHERE "TagName" = 'Sci-fi';
    SELECT "TagID" INTO tag_id_survival FROM public."Tags" WHERE "TagName" = 'Survival';
    SELECT "TagID" INTO tag_id_crafting FROM public."Tags" WHERE "TagName" = 'Crafting';
    SELECT "TagID" INTO tag_id_simulation FROM public."Tags" WHERE "TagName" = 'Simulation';
    SELECT "TagID" INTO tag_id_platformer FROM public."Tags" WHERE "TagName" = 'Platformer';
    SELECT "TagID" INTO tag_id_puzzle FROM public."Tags" WHERE "TagName" = 'Puzzle';
    SELECT "TagID" INTO tag_id_co_op FROM public."Tags" WHERE "TagName" = 'Co-op';
    SELECT "TagID" INTO tag_id_fps FROM public."Tags" WHERE "TagName" = 'FPS';
    SELECT "TagID" INTO tag_id_anime FROM public."Tags" WHERE "TagName" = 'Anime';
    SELECT "TagID" INTO tag_id_racing FROM public."Tags" WHERE "TagName" = 'Racing';
    SELECT "TagID" INTO tag_id_casual FROM public."Tags" WHERE "TagName" = 'Casual';
    RAISE NOTICE 'Hoàn thành l?y ID c?a các Tags.';

    -- Thêm Games và l?y ID tr? v?
    RAISE NOTICE 'Ðang chèn Games...';
    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_microsoft_id, 'Age of Empires', '1997-10-26', TRUE, 9.99, 'Classic real-time strategy game spanning 10,000 years of history.', 'Age of Empires is the critically acclaimed, award winning real-time strategy game that launched a 20-year legacy. Command one of 12 ancient civilizations from the Stone Age to the Iron Age.', 0, 'Windows 95/98/NT', '80MB', 'Pentium 90MHz', '16MB RAM', 'A classic that runs on almost anything modern via compatibility modes or remasters.', 'SVGA 1MB')
    RETURNING "GameID" INTO game_id_aoe;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_toby_fox_id, 'Undertale', '2015-09-15', TRUE, 9.99, 'The RPG game where you don''t have to destroy anyone.', 'UNDERTALE! The RPG game where you don''t have to destroy anyone. Fall into the world of monsters and find your way out... or stay trapped forever. Features a unique battle system, memorable characters, and a critically acclaimed soundtrack.', 0, 'Windows XP, Vista, 7, 8, or 10', '200 MB available space', '2GHz+', '2 GB RAM', 'Undertale is not a very demanding game.', '128MB dedicated VRAM')
    RETURNING "GameID" INTO game_id_undertale;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_kikiyama_id, 'Yume Nikki', '2004-06-26', TRUE, 0.00, 'A surreal adventure RPG where you explore the vast and bizarre dream worlds of a young girl.', 'Yume Nikki is a cult classic freeware game developed by Kikiyama. Players navigate the dreams of Madotsuki, collecting "Effects" to alter her appearance and abilities, unlocking new areas to explore. Known for its atmospheric and abstract world.', 0, 'Windows', '50MB', 'Any modern CPU', '256MB RAM', 'Very low system requirements.', 'Integrated graphics')
    RETURNING "GameID" INTO game_id_yume_nikki;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Half-Life 2', '2004-11-16', TRUE, 9.99, 'Award-winning sci-fi FPS known for its innovative physics engine.', 'Half-Life 2 is a 2004 first-person shooter game developed by Valve Corporation. It is the sequel to 1998''s Half-Life and was praised for its advanced physics engine, graphics, and narrative.', 0, 'Windows XP/Vista/7/8/10', '7 GB', '1.7 GHz Processor', '512 MB RAM', 'Requires Steam account.', 'DirectX 7 compatible graphics card')
    RETURNING "GameID" INTO game_id_half_life2;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_cd_projekt_id, 'The Witcher 3: Wild Hunt', '2015-05-19', TRUE, 39.99, 'An open-world action RPG with a massive world and compelling story.', 'The Witcher 3: Wild Hunt is a story-driven open world RPG set in a visually stunning fantasy universe full of meaningful choices and impactful consequences. Play as professional monster hunter Geralt of Rivia.', 0, 'Windows 7/8/10 (64-bit)', '35 GB', 'Intel Core i5-2500K 3.3 GHz / AMD Phenom II X4 940', '6 GB RAM', 'Highly acclaimed for its narrative and world-building.', 'NVIDIA GeForce GTX 660 / AMD Radeon HD 7870')
    RETURNING "GameID" INTO game_id_witcher3;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_mojang_id, 'Minecraft', '2011-11-18', TRUE, 26.95, 'A sandbox game where you can build anything you imagine.', 'Minecraft is a sandbox video game developed by Mojang Studios. In Minecraft, players explore a blocky, procedurally generated 3D world with infinite terrain, and may discover and extract raw materials, craft tools and items, and build structures, earthworks, and machines.', 0, 'Windows 7/8/10', '1 GB', 'Intel Core i3-3210 / AMD A8-7600 APU', '4 GB RAM', 'Java Edition recommended for modding.', 'Integrated Graphics')
    RETURNING "GameID" INTO game_id_minecraft;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_nintendo_id, 'Mario Kart 8 Deluxe', '2017-04-28', TRUE, 59.99, 'Race your friends and family with iconic Mario characters.', 'Hit the road with the definitive version of Mario Kart 8, and play anytime, anywhere! Race your friends or battle them in a revised battle mode on new and returning battle courses.', 0, 'Nintendo Switch', '7 GB', 'Nintendo Switch CPU', '4 GB RAM', 'Exclusive to Nintendo Switch.', 'Integrated NVIDIA Tegra X1')
    RETURNING "GameID" INTO game_id_mario_kart8;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Portal 2', '2011-04-19', TRUE, 19.99, 'A critically acclaimed puzzle-platform video game with unique mechanics.', 'Portal 2 draws from the award-winning formula of innovative gameplay, story, and music that earned the original Portal over 70 industry accolades and created a cult following. The single-player portion of Portal 2 introduces a cast of dynamic new characters, a host of fresh puzzle elements, and a much larger set of devious test chambers.', 0, 'Windows 7/Vista/XP', '8 GB', 'AMD64X2 (or higher)', '2 GB RAM', 'Features a highly praised co-op mode.', '128 MB or more, Shader Model 2.0 capable')
    RETURNING "GameID" INTO game_id_portal2;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_cd_projekt_id, 'Cyberpunk 2077', '2020-12-10', TRUE, 59.99, 'An open-world action-adventure RPG set in the megalopolis of Night City.', 'Cyberpunk 2077 is an open-world, action-adventure RPG set in Night City, a megalopolis obsessed with power, glamour and body modification. You play as V, a mercenary outlaw going after a one-of-a-kind implant that is the key to immortality.', 0, '64-bit Windows 10', '70 GB SSD', 'Intel Core i7-4790 or AMD Ryzen 3 3200G', '12 GB RAM', 'Known for its stunning graphics and deep narrative.', 'NVIDIA GeForce GTX 1060 6GB or AMD Radeon R9 Fury')
    RETURNING "GameID" INTO game_id_cyberpunk2077;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Stardew Valley', '2016-02-26', TRUE, 14.99, 'A charming farming and life simulation RPG.', 'You''ve inherited your grandfather''s old farm plot in Stardew Valley. Armed with hand-me-down tools and a few coins, you set out to begin your new life. Can you learn to live off the land and turn these overgrown fields into a thriving home?', 0, 'Windows Vista or greater', '500 MB', '2 Ghz', '2 GB RAM', 'Relaxing and highly replayable.', '256 mb video memory, shader model 3.0+')
    RETURNING "GameID" INTO game_id_stardew_valley;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_toby_fox_id, 'Deltarune Chapter 1 & 2', '2018-10-31', TRUE, 0.00, 'The next adventure from the creator of Undertale.', 'DELTARUNE is a role-playing video game by Toby Fox. It is a spiritual successor to Undertale, featuring similar gameplay mechanics and a distinct art style. Chapters 1 and 2 are currently available for free.', 0, 'Windows 7/8/10', '200 MB', '2GHz+', '2 GB RAM', 'Free to play, episodic releases.', '128MB dedicated VRAM')
    RETURNING "GameID" INTO game_id_deltarune;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_kikiyama_id, 'Among Us', '2018-11-16', TRUE, 4.99, 'A social deduction game where you find the impostor among your crewmates.', 'Play with 4-15 players online or via local WiFi as you attempt to prepare your spaceship for departure, but beware as one or more random players among the Crewmates are Impostors bent on killing everyone!', 0, 'Windows 7 SP1+', '250 MB', 'Intel Pentium E2180', '1 GB RAM', 'Popular for its multiplayer and deception gameplay.', 'Intel HD Graphics')
    RETURNING "GameID" INTO game_id_among_us;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Hollow Knight', '2017-02-24', TRUE, 14.99, 'An epic action-adventure set in a vast, ruined insect kingdom.', 'Brave the depths of a forgotten kingdom in Hollow Knight! Explore twisting caverns, ancient cities and deadly wastes; battle tainted creatures and befriended bizarre bugs; and solve the ancient mysteries at the kingdom''s heart.', 0, 'Windows 7', '9 GB', 'Intel Core i3', '4 GB RAM', 'Known for its challenging combat and beautiful art style.', 'GeForce GTX 560 / Radeon HD 5770')
    RETURNING "GameID" INTO game_id_hollow_knight;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Celeste', '2018-01-25', TRUE, 19.99, 'A challenging platformer about climbing a mountain and overcoming personal struggles.', 'Help Madeline survive her inner demons on her journey to the top of Celeste Mountain in this super-tight, hand-crafted platformer from the creators of TowerFall.', 0, 'Windows 7+', '1.2 GB', 'Intel Core i3 M380', '2 GB RAM', 'Critically acclaimed for its gameplay and story.', 'Intel HD 4000')
    RETURNING "GameID" INTO game_id_celeste;

    INSERT INTO public."Game" ("PublisherID", "Name", "ReleaseDate", "State", "Price", "ShortDescription", "FullDescription", "TotalPurchased", "OS", "Storage", "Processor", "Memory", "AdditionalNotes", "Graphics")
    VALUES (user_valve_id, 'Outer Wilds', '2019-05-29', TRUE, 24.99, 'Explore a mysterious solar system trapped in a time loop.', 'A curious space exploration game about a solar system trapped in an endless time loop. Unravel the mysteries of the universe and piece together the history of a vanished civilization.', 0, 'Windows 7', '8 GB', 'Intel Core i5-2300 | AMD FX-4350', '6 GB RAM', 'A unique and highly atmospheric exploration game.', 'Geforce GTX 560 TI | Radeon HD 6870')
    RETURNING "GameID" INTO game_id_outer_wilds;
    RAISE NOTICE 'Hoàn thành chèn Games.';

    -- Gán Tags cho Games
    RAISE NOTICE 'Ðang chèn GameTags...';
    INSERT INTO public."GameTags" ("GameID", "TagID") VALUES
    (game_id_aoe, tag_id_rts), (game_id_aoe, tag_id_strategy), (game_id_aoe, tag_id_historical), (game_id_aoe, tag_id_classic), (game_id_aoe, tag_id_singleplayer), (game_id_aoe, tag_id_multiplayer),
    (game_id_undertale, tag_id_rpg), (game_id_undertale, tag_id_indie), (game_id_undertale, tag_id_story_rich), (game_id_undertale, tag_id_great_soundtrack), (game_id_undertale, tag_id_pixel_graphics), (game_id_undertale, tag_id_choices_matter), (game_id_undertale, tag_id_adventure),
    (game_id_yume_nikki, tag_id_indie), (game_id_yume_nikki, tag_id_surreal), (game_id_yume_nikki, tag_id_horror), (game_id_yume_nikki, tag_id_exploration), (game_id_yume_nikki, tag_id_adventure), (game_id_yume_nikki, tag_id_free_to_play),
    (game_id_half_life2, tag_id_action), (game_id_half_life2, tag_id_fps), (game_id_half_life2, tag_id_sci_fi), (game_id_half_life2, tag_id_singleplayer), (game_id_half_life2, tag_id_classic),
    (game_id_witcher3, tag_id_rpg), (game_id_witcher3, tag_id_open_world), (game_id_witcher3, tag_id_fantasy), (game_id_witcher3, tag_id_story_rich), (game_id_witcher3, tag_id_action), (game_id_witcher3, tag_id_adventure),
    (game_id_minecraft, tag_id_survival), (game_id_minecraft, tag_id_crafting), (game_id_minecraft, tag_id_multiplayer), (game_id_minecraft, tag_id_singleplayer), (game_id_minecraft, tag_id_open_world),
    (game_id_mario_kart8, tag_id_multiplayer), (game_id_mario_kart8, tag_id_simulation), (game_id_mario_kart8, tag_id_racing), (game_id_mario_kart8, tag_id_casual),
    (game_id_portal2, tag_id_puzzle), (game_id_portal2, tag_id_singleplayer), (game_id_portal2, tag_id_co_op), (game_id_portal2, tag_id_sci_fi), (game_id_portal2, tag_id_adventure),
    (game_id_cyberpunk2077, tag_id_rpg), (game_id_cyberpunk2077, tag_id_open_world), (game_id_cyberpunk2077, tag_id_sci_fi), (game_id_cyberpunk2077, tag_id_action), (game_id_cyberpunk2077, tag_id_story_rich),
    (game_id_stardew_valley, tag_id_indie), (game_id_stardew_valley, tag_id_simulation), (game_id_stardew_valley, tag_id_rpg), (game_id_stardew_valley, tag_id_pixel_graphics), (game_id_stardew_valley, tag_id_singleplayer),
    (game_id_deltarune, tag_id_rpg), (game_id_deltarune, tag_id_indie), (game_id_deltarune, tag_id_story_rich), (game_id_deltarune, tag_id_pixel_graphics), (game_id_deltarune, tag_id_great_soundtrack), (game_id_deltarune, tag_id_free_to_play),
    (game_id_among_us, tag_id_multiplayer), (game_id_among_us, tag_id_indie), (game_id_among_us, tag_id_strategy), (game_id_among_us, tag_id_casual),
    (game_id_hollow_knight, tag_id_indie), (game_id_hollow_knight, tag_id_platformer), (game_id_hollow_knight, tag_id_adventure), (game_id_hollow_knight, tag_id_exploration), (game_id_hollow_knight, tag_id_action),
    (game_id_celeste, tag_id_indie), (game_id_celeste, tag_id_platformer), (game_id_celeste, tag_id_adventure), (game_id_celeste, tag_id_singleplayer), (game_id_celeste, tag_id_pixel_graphics),
    (game_id_outer_wilds, tag_id_indie), (game_id_outer_wilds, tag_id_exploration), (game_id_outer_wilds, tag_id_adventure), (game_id_outer_wilds, tag_id_puzzle), (game_id_outer_wilds, tag_id_sci_fi);
    RAISE NOTICE 'Hoàn thành chèn GameTags.';

    -- Thêm Media
    RAISE NOTICE 'Ðang chèn Media...';
    INSERT INTO public."Media" ("GameID", "Url", "Type") VALUES
    (game_id_aoe, 'https://gepig.com/game_cover_460w/148.jpg', 'image_header'),
    (game_id_aoe, 'https://cdn.akamai.steamstatic.com/steam/apps/221380/ss_7e3c8c9f1e7c8b3a1c6b9a9b5a4d7f8c8e6f7a0e.600x338.jpg', 'screenshot'),
    (game_id_undertale, 'https://cdn.akamai.steamstatic.com/steam/apps/391540/header.jpg?t=1579096091', 'image_header'),
    (game_id_undertale, 'https://cdn.akamai.steamstatic.com/steam/apps/391540/ss_06ba6887176238367393e029f914dd29a0708179.600x338.jpg', 'screenshot'),
    (game_id_undertale, 'https://cdn.akamai.steamstatic.com/steam/apps/391540/ss_1a8f0462894796847f0f995f770720202d4de607.600x338.jpg?t=1579096091', 'screenshot'),
    (game_id_yume_nikki, 'https://cdn.akamai.steamstatic.com/steam/apps/650700/header.jpg?t=1667508284', 'image_header'),
    (game_id_yume_nikki, 'https://cdn.akamai.steamstatic.com/steam/apps/650700/ss_03238751979421b7f7d553391c4e3843356f46ba.600x338.jpg?t=1667508284', 'screenshot'),
    (game_id_half_life2, 'https://cdn.akamai.steamstatic.com/steam/apps/220/header.jpg?t=1678129757', 'image_header'),
    (game_id_half_life2, 'https://cdn.akamai.steamstatic.com/steam/apps/220/ss_8e7a0b3c4f5d6a7b8c9d0e1f2a3b4c5d6e7f8a9b.600x338.jpg', 'screenshot'),
    (game_id_witcher3, 'https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg?t=1690899047', 'image_header'),
    (game_id_witcher3, 'https://cdn.akamai.steamstatic.com/steam/apps/292030/ss_c7e3f2d1a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9.600x338.jpg', 'screenshot'),
    (game_id_minecraft, 'https://www.minecraft.net/content/dam/minecraft/pdp/Minecraft_Render_Keyart.png', 'image_header'),
    (game_id_minecraft, 'https://cdn.mos.cms.futurecdn.net/tVv26e632gM7JzSgT6eFwA.jpg', 'screenshot'),
    (game_id_mario_kart8, 'https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/description_image_mario_kart_8_deluxe_C01.jpg', 'image_header'),
    (game_id_mario_kart8, 'https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/screenshot01.jpg', 'screenshot'),
    (game_id_portal2, 'https://cdn.akamai.steamstatic.com/steam/apps/620/header.jpg?t=1667084534', 'image_header'),
    (game_id_portal2, 'https://cdn.akamai.steamstatic.com/steam/apps/620/ss_b8c7a6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9.600x338.jpg', 'screenshot'),
    (game_id_cyberpunk2077, 'https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg?t=1697626947', 'image_header'),
    (game_id_cyberpunk2077, 'https://cdn.akamai.steamstatic.com/steam/apps/1091500/ss_9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b.600x338.jpg', 'screenshot'),
    (game_id_stardew_valley, 'https://cdn.akamai.steamstatic.com/steam/apps/413150/header.jpg?t=1697232230', 'image_header'),
    (game_id_stardew_valley, 'https://cdn.akamai.steamstatic.com/steam/apps/413150/ss_4070a78d052d9c0a6b7c8d9e0f1a2b3c4d5e6f7a.600x338.jpg', 'screenshot'),
    (game_id_deltarune, 'https://cdn.akamai.steamstatic.com/steam/apps/1671210/header.jpg?t=1676678401', 'image_header'),
    (game_id_deltarune, 'https://cdn.akamai.steamstatic.com/steam/apps/1671210/ss_e1f0d9c8b7a6543210fedcbafedcba9876543210.600x338.jpg', 'screenshot'),
    (game_id_among_us, 'https://cdn.akamai.steamstatic.com/steam/apps/945360/header.jpg?t=1680196230', 'image_header'),
    (game_id_among_us, 'https://cdn.akamai.steamstatic.com/steam/apps/945360/ss_b5a4d3c2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6.600x338.jpg', 'screenshot'),
    (game_id_hollow_knight, 'https://cdn.akamai.steamstatic.com/steam/apps/367520/header.jpg?t=1667954930', 'image_header'),
    (game_id_hollow_knight, 'https://cdn.akamai.steamstatic.com/steam/apps/367520/ss_f0e9d8c7b6a543210fedcba9876543210abcdef.600x338.jpg', 'screenshot'),
    (game_id_celeste, 'https://cdn.akamai.steamstatic.com/steam/apps/504230/header.jpg?t=1688661649', 'image_header'),
    (game_id_celeste, 'https://cdn.akamai.steamstatic.com/steam/apps/504230/ss_a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0.600x338.jpg', 'screenshot'),
    (game_id_outer_wilds, 'https://cdn.akamai.steamstatic.com/steam/apps/753640/header.jpg?t=1697241280', 'image_header'),
    (game_id_outer_wilds, 'https://cdn.akamai.steamstatic.com/steam/apps/753640/ss_b9a8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0.600x338.jpg', 'screenshot');
    RAISE NOTICE 'Hoàn thành chèn Media.';
    RAISE NOTICE 'ÐÃ CHÈN THÀNH CÔNG T?T C? D? LI?U M?U.';

END $$;

-- Chèn 2 ngu?i dùng cu?i. 
-- RoleID '2' cho Publisher và '3' cho Admin du?c gi? d?nh d?a trên th? t? trong migrate.sql
INSERT INTO public."User" (
    "RoleID", "Email", "Username", "Password", "AvatarURL", "CreatedAt", "WalletBalance",
    "Country", "DoB", "Gender", "ProfileName", "ThemeName", "BackgroundName", "Summary",
    "BanStatus", "NewEmailAddress", "EmailChangeToken", "EmailChangeTokenExpiry"
)
VALUES (
    2, -- 'Publisher'
    'syhuytran2005@gmail.com',
    'kerri',
    '$2a$10$/ijdwR3ZK9Ic1HBNF.gNmuxvP621YUc8JQ49iHMjW6mGDO6KWHJMi',
    NULL, NULL, 0.00, 'Vietnam', NULL, NULL, NULL, NULL, NULL, NULL,
    FALSE, -- BanStatus 0 trong T-SQL là FALSE
    NULL, NULL, NULL
);

INSERT INTO public."User" (
    "RoleID", "Email", "Username", "Password", "AvatarURL", "CreatedAt", "WalletBalance",
    "Country", "DoB", "Gender", "ProfileName", "ThemeName", "BackgroundName", "Summary",
    "BanStatus", "NewEmailAddress", "EmailChangeToken", "EmailChangeTokenExpiry"
)
VALUES (
    3, -- 'Admin'
    'syhuytran2@gmail.com',
    'kerri1',
    '$2a$10$/ijdwR3ZK9Ic1HBNF.gNmuxvP621YUc8JQ49iHMjW6mGDO6KWHJMi',
    NULL, NULL, 0.00, 'Vietnam', NULL, NULL, NULL, NULL, NULL, NULL,
    FALSE, -- BanStatus 0 trong T-SQL là FALSE
    NULL, NULL, NULL
);


