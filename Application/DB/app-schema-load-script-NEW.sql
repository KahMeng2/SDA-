-- Set the search path to the app schema
SET search_path TO app;

-- Insert data into faculty_member table (30 rows)
INSERT INTO faculty_member (role, department, email, username, password, first_name, last_name, middle_name, date_of_birth)
VALUES 
('Professor', 'Computer Science', 'john.doe@unimelb.edu.au', 'johndoe', 'hashed_password', 'John', 'Doe', 'Michael', '1975-05-15'),
('Associate Professor', 'Physics', 'jane.smith@unimelb.edu.au', 'janesmith', 'hashed_password', 'Jane', 'Smith', NULL, '1980-09-22'),
('Lecturer', 'Mathematics', 'alex.wong@unimelb.edu.au', 'alexwong', 'hashed_password', 'Alex', 'Wong', 'Lee', '1985-03-30'),
('Professor', 'Chemistry', 'sarah.johnson@unimelb.edu.au', 'sarahj', 'hashed_password', 'Sarah', 'Johnson', NULL, '1972-11-18'),
('Senior Lecturer', 'Biology', 'michael.brown@unimelb.edu.au', 'michaelb', 'hashed_password', 'Michael', 'Brown', 'James', '1978-07-04'),
('Professor', 'Engineering', 'emily.taylor@unimelb.edu.au', 'emilyt', 'hashed_password', 'Emily', 'Taylor', 'Rose', '1970-01-25'),
('Lecturer', 'Psychology', 'david.wilson@unimelb.edu.au', 'davidw', 'hashed_password', 'David', 'Wilson', NULL, '1988-06-12'),
('Associate Professor', 'Economics', 'lisa.chen@unimelb.edu.au', 'lisac', 'hashed_password', 'Lisa', 'Chen', 'Ming', '1982-09-03'),
('Professor', 'Law', 'robert.miller@unimelb.edu.au', 'robertm', 'hashed_password', 'Robert', 'Miller', 'John', '1968-12-30'),
('Senior Lecturer', 'History', 'amanda.green@unimelb.edu.au', 'amandag', 'hashed_password', 'Amanda', 'Green', NULL, '1979-04-17'),
('Lecturer', 'English', 'thomas.lee@unimelb.edu.au', 'thomasl', 'hashed_password', 'Thomas', 'Lee', 'William', '1986-08-22'),
('Professor', 'Medicine', 'olivia.white@unimelb.edu.au', 'oliviaw', 'hashed_password', 'Olivia', 'White', 'Grace', '1971-03-09'),
('Associate Professor', 'Sociology', 'daniel.nguyen@unimelb.edu.au', 'danieln', 'hashed_password', 'Daniel', 'Nguyen', 'Van', '1977-10-05'),
('Lecturer', 'Art History', 'sophia.patel@unimelb.edu.au', 'sophiap', 'hashed_password', 'Sophia', 'Patel', NULL, '1989-02-14'),
('Professor', 'Political Science', 'william.jackson@unimelb.edu.au', 'williamj', 'hashed_password', 'William', 'Jackson', 'Thomas', '1969-07-28'),
('Senior Lecturer', 'Environmental Science', 'emma.rodriguez@unimelb.edu.au', 'emmar', 'hashed_password', 'Emma', 'Rodriguez', 'Maria', '1981-11-11'),
('Lecturer', 'Music', 'james.kim@unimelb.edu.au', 'jamesk', 'hashed_password', 'James', 'Kim', 'Sung', '1987-05-20'),
('Professor', 'Anthropology', 'laura.murphy@unimelb.edu.au', 'lauram', 'hashed_password', 'Laura', 'Murphy', 'Anne', '1973-09-08'),
('Associate Professor', 'Philosophy', 'kevin.zhang@unimelb.edu.au', 'kevinz', 'hashed_password', 'Kevin', 'Zhang', 'Wei', '1976-12-03'),
('Lecturer', 'Linguistics', 'natalie.cohen@unimelb.edu.au', 'nataliec', 'hashed_password', 'Natalie', 'Cohen', NULL, '1990-01-31'),
('Professor', 'Astronomy', 'christopher.singh@unimelb.edu.au', 'chriss', 'hashed_password', 'Christopher', 'Singh', 'Raj', '1967-06-19'),
('Senior Lecturer', 'Geography', 'rachel.thompson@unimelb.edu.au', 'rachelt', 'hashed_password', 'Rachel', 'Thompson', 'Lynn', '1983-04-26'),
('Lecturer', 'Statistics', 'eric.martinez@unimelb.edu.au', 'ericm', 'hashed_password', 'Eric', 'Martinez', NULL, '1988-10-15'),
('Professor', 'Biochemistry', 'jennifer.wong@unimelb.edu.au', 'jenniferw', 'hashed_password', 'Jennifer', 'Wong', 'Mei', '1974-02-07'),
('Associate Professor', 'Archaeology', 'brian.o''connor@unimelb.edu.au', 'briano', 'hashed_password', 'Brian', 'O''Connor', 'Patrick', '1979-08-23'),
('Lecturer', 'Media Studies', 'michelle.kim@unimelb.edu.au', 'michellek', 'hashed_password', 'Michelle', 'Kim', 'Soo', '1991-03-12'),
('Professor', 'Neuroscience', 'andrew.taylor@unimelb.edu.au', 'andrewt', 'hashed_password', 'Andrew', 'Taylor', 'James', '1970-05-29'),
('Senior Lecturer', 'Education', 'katherine.chen@unimelb.edu.au', 'katherinec', 'hashed_password', 'Katherine', 'Chen', 'Li', '1984-07-16'),
('Lecturer', 'Film Studies', 'paul.nguyen@unimelb.edu.au', 'pauln', 'hashed_password', 'Paul', 'Nguyen', 'Minh', '1989-09-04'),
('Professor', 'Geology', 'elizabeth.brown@unimelb.edu.au', 'elizabethb', 'hashed_password', 'Elizabeth', 'Brown', 'Marie', '1972-12-21');

-- Insert data into student table (30 rows)
INSERT INTO student (email, username, password, first_name, last_name, middle_name, date_of_birth)
VALUES 
('alice.johnson@student.unimelb.edu.au', 'alicej', 'hashed_password', 'Alice', 'Johnson', 'Marie', '1998-03-10'),
('bob.williams@student.unimelb.edu.au', 'bobw', 'hashed_password', 'Bob', 'Williams', NULL, '1999-11-28'),
('charlie.brown@student.unimelb.edu.au', 'charlieb', 'hashed_password', 'Charlie', 'Brown', 'David', '2000-05-15'),
('diana.lee@student.unimelb.edu.au', 'dianal', 'hashed_password', 'Diana', 'Lee', 'Sue', '1999-07-22'),
('ethan.nguyen@student.unimelb.edu.au', 'ethann', 'hashed_password', 'Ethan', 'Nguyen', 'Van', '2001-01-30'),
('fiona.smith@student.unimelb.edu.au', 'fionas', 'hashed_password', 'Fiona', 'Smith', NULL, '2000-09-18'),
('george.patel@student.unimelb.edu.au', 'georgep', 'hashed_password', 'George', 'Patel', 'Raj', '1999-04-05'),
('hannah.kim@student.unimelb.edu.au', 'hannahk', 'hashed_password', 'Hannah', 'Kim', 'Min', '2001-06-12'),
('ian.chen@student.unimelb.edu.au', 'ianc', 'hashed_password', 'Ian', 'Chen', 'Wei', '2000-02-28'),
('julia.garcia@student.unimelb.edu.au', 'juliag', 'hashed_password', 'Julia', 'Garcia', 'Maria', '1999-10-09'),
('kevin.taylor@student.unimelb.edu.au', 'kevint', 'hashed_password', 'Kevin', 'Taylor', 'John', '2001-03-17'),
('laura.wong@student.unimelb.edu.au', 'lauraw', 'hashed_password', 'Laura', 'Wong', 'Mei', '2000-08-25'),
('michael.o''brien@student.unimelb.edu.au', 'michaelo', 'hashed_password', 'Michael', 'O''Brien', 'Patrick', '1999-12-03'),
('natalie.singh@student.unimelb.edu.au', 'natalies', 'hashed_password', 'Natalie', 'Singh', 'Priya', '2001-05-20'),
('oliver.brown@student.unimelb.edu.au', 'oliverb', 'hashed_password', 'Oliver', 'Brown', NULL, '2000-01-14'),
('patricia.lee@student.unimelb.edu.au', 'patricial', 'hashed_password', 'Patricia', 'Lee', 'Yoon', '1999-09-30'),
('quinn.murphy@student.unimelb.edu.au', 'quinnm', 'hashed_password', 'Quinn', 'Murphy', 'Rose', '2001-07-08'),
('ryan.nguyen@student.unimelb.edu.au', 'ryann', 'hashed_password', 'Ryan', 'Nguyen', 'Minh', '2000-04-22'),
('sophia.chen@student.unimelb.edu.au', 'sophiac', 'hashed_password', 'Sophia', 'Chen', 'Li', '1999-11-11'),
('thomas.wilson@student.unimelb.edu.au', 'thomasw', 'hashed_password', 'Thomas', 'Wilson', 'James', '2001-02-06'),
('ursula.kim@student.unimelb.edu.au', 'ursulak', 'hashed_password', 'Ursula', 'Kim', 'Soo', '2000-10-19'),
('victor.patel@student.unimelb.edu.au', 'victorp', 'hashed_password', 'Victor', 'Patel', NULL, '1999-06-27'),
('wendy.zhang@student.unimelb.edu.au', 'wendyz', 'hashed_password', 'Wendy', 'Zhang', 'Hui', '2001-08-14'),
('xavier.garcia@student.unimelb.edu.au', 'xavierg', 'hashed_password', 'Xavier', 'Garcia', 'Luis', '2000-03-03'),
('yasmin.taylor@student.unimelb.edu.au', 'yasmint', 'hashed_password', 'Yasmin', 'Taylor', 'Grace', '1999-12-31'),
('zack.wong@student.unimelb.edu.au', 'zackw', 'hashed_password', 'Zack', 'Wong', 'Kai', '2001-04-09'),
('amy.o''connor@student.unimelb.edu.au', 'amyo', 'hashed_password', 'Amy', 'O''Connor', 'Fiona', '2000-11-23'),
('ben.singh@student.unimelb.edu.au', 'bens', 'hashed_password', 'Ben', 'Singh', 'Arjun', '1999-07-01'),
('chloe.brown@student.unimelb.edu.au', 'chloeb', 'hashed_password', 'Chloe', 'Brown', 'Elizabeth', '2001-01-18'),
('daniel.lee@student.unimelb.edu.au', 'daniell', 'hashed_password', 'Daniel', 'Lee', 'Jae', '2000-06-05');

-- Insert data into club table (30 rows)
INSERT INTO club (name, balance)
VALUES 
('Computer Science Club', 1000.00),
('Physics Society', 750.50),
('Mathematics Association', 1200.75),
('Chemistry Club', 900.25),
('Biology Society', 1100.00),
('Engineering Club', 1500.50),
('Psychology Students Association', 800.75),
('Economics Society', 950.00),
('Law Students Society', 1300.25),
('History Club', 700.50),
('English Literature Society', 850.75),
('Medical Students Association', 1400.00),
('Sociology Club', 600.25),
('Art History Society', 750.00),
('Political Science Association', 1000.50),
('Environmental Science Club', 900.75),
('Music Society', 1150.00),
('Anthropology Club', 800.25),
('Philosophy Society', 700.00),
('Linguistics Club', 850.50),
('Astronomy Society', 1250.75),
('Geography Club', 950.25),
('Statistics Association', 1050.00),
('Biochemistry Society', 1100.50),
('Archaeology Club', 800.75),
('Media Studies Society', 900.00),
('Neuroscience Club', 1300.25),
('Education Students Association', 750.50),
('Film Society', 1000.75),
('Geology Club', 1150.25);

-- Insert data into admin_role table (at least 10 entries)
INSERT INTO admin_role (student_id, club_id)
VALUES 
((SELECT id FROM student WHERE username = 'alicej'), (SELECT id FROM club WHERE name = 'Computer Science Club')),
((SELECT id FROM student WHERE username = 'bobw'), (SELECT id FROM club WHERE name = 'Physics Society')),
((SELECT id FROM student WHERE username = 'charlieb'), (SELECT id FROM club WHERE name = 'Mathematics Association')),
((SELECT id FROM student WHERE username = 'dianal'), (SELECT id FROM club WHERE name = 'Chemistry Club')),
((SELECT id FROM student WHERE username = 'ethann'), (SELECT id FROM club WHERE name = 'Biology Society')),
((SELECT id FROM student WHERE username = 'fionas'), (SELECT id FROM club WHERE name = 'Engineering Club')),
((SELECT id FROM student WHERE username = 'georgep'), (SELECT id FROM club WHERE name = 'Psychology Students Association')),
((SELECT id FROM student WHERE username = 'hannahk'), (SELECT id FROM club WHERE name = 'Economics Society')),
((SELECT id FROM student WHERE username = 'ianc'), (SELECT id FROM club WHERE name = 'Law Students Society')),
((SELECT id FROM student WHERE username = 'juliag'), (SELECT id FROM club WHERE name = 'History Club'));

-- Insert data into address table (30 rows)
INSERT INTO address (address_line_1, address_line_2, city, state, country, postcode)
VALUES 
('123 Swanston Street', 'Building A', 'Melbourne', 'Victoria', 'Australia', 3000),
('456 Lygon Street', NULL, 'Carlton', 'Victoria', 'Australia', 3053),
('789 Elizabeth Street', 'Room 101', 'Melbourne', 'Victoria', 'Australia', 3000),
('321 Grattan Street', NULL, 'Parkville', 'Victoria', 'Australia', 3052),
('654 Cardigan Street', 'Level 2', 'Carlton', 'Victoria', 'Australia', 3053),
('987 Royal Parade', NULL, 'Parkville', 'Victoria', 'Australia', 3052),
('147 Pelham Street', 'Suite 5', 'Carlton', 'Victoria', 'Australia', 3053),
('258 Drummond Street', NULL, 'Carlton', 'Victoria', 'Australia', 3053),
('369 Rathdowne Street', 'Unit 3', 'Carlton', 'Victoria', 'Australia', 3053),
('741 Elgin Street', NULL, 'Carlton', 'Victoria', 'Australia', 3053),
('852 Queensberry Street', 'Room 202', 'Carlton', 'Victoria', 'Australia', 3053),
('963 Bouverie Street', NULL, 'Carlton', 'Victoria', 'Australia', 3053),
('159 Berkeley Street', 'Building B', 'Carlton', 'Victoria', 'Australia', 3053),
('753 Faraday Street', NULL, 'Carlton', 'Victoria', 'Australia', 3053),
('951 Cardigan Street', 'Level 3', 'Carlton', 'Victoria', 'Australia', 3053),
('357 Swanston Street', NULL, 'Melbourne', 'Victoria', 'Australia', 3000),
('864 Russell Street', 'Room 303', 'Melbourne', 'Victoria', 'Australia', 3000),
('246 Exhibition Street', NULL, 'Melbourne', 'Victoria', 'Australia', 3000),
('135 LaTrobe Street', 'Suite 7', 'Melbourne', 'Victoria', 'Australia', 3000),
('579 Lonsdale Street', NULL, 'Melbourne', 'Victoria', 'Australia', 3000),
('468 Little Collins Street', 'Unit 4', 'Melbourne', 'Victoria', 'Australia', 3000),
('792 Flinders Street', NULL, 'Melbourne', 'Victoria', 'Australia', 3000),
('513 Spencer Street', 'Room 404', 'Melbourne', 'Victoria', 'Australia', 3000),
('624 King Street', NULL, 'West Melbourne', 'Victoria', 'Australia', 3003),
('735 Dryburgh Street', 'Building C', 'North Melbourne', 'Victoria', 'Australia', 3051),
('846 Abbotsford Street', NULL, 'North Melbourne', 'Victoria', 'Australia', 3051),
('957 Courtney Street', 'Level 4', 'North Melbourne', 'Victoria', 'Australia', 3051),
('168 Flemington Road', NULL, 'North Melbourne', 'Victoria', 'Australia', 3051),
('279 Peel Street', 'Suite 9', 'North Melbourne', 'Victoria', 'Australia', 3051),
('381 Victoria Street', NULL, 'North Melbourne', 'Victoria', 'Australia', 3051);

-- Insert data into physical_venue table (30 rows) with address_id obtained from select queries
INSERT INTO physical_venue (description, cost, venue_capacity, address_id, floor, room)
VALUES 
('Large Auditorium', 500.00, 200, (SELECT id FROM address WHERE address_line_1 = '123 Swanston Street' AND address_line_2 = 'Building A' AND city = 'Melbourne'), '1st Floor', 'Room 101'),
('Small Conference Room', 100.00, 20, (SELECT id FROM address WHERE address_line_1 = '456 Lygon Street' AND address_line_2 IS NULL AND city = 'Carlton'), '2nd Floor', 'Room 201'),
('Lecture Theatre', 300.00, 150, (SELECT id FROM address WHERE address_line_1 = '789 Elizabeth Street' AND address_line_2 = 'Room 101' AND city = 'Melbourne'), 'Ground Floor', 'Theatre 1'),
('Computer Lab', 200.00, 50, (SELECT id FROM address WHERE address_line_1 = '321 Grattan Street' AND address_line_2 IS NULL AND city = 'Parkville'), '3rd Floor', 'Lab 301'),
('Seminar Room', 150.00, 30, (SELECT id FROM address WHERE address_line_1 = '654 Cardigan Street' AND address_line_2 = 'Level 2' AND city = 'Carlton'), '4th Floor', 'Room 401'),
('Exhibition Hall', 1000.00, 500, (SELECT id FROM address WHERE address_line_1 = '987 Royal Parade' AND address_line_2 IS NULL AND city = 'Parkville'), 'Ground Floor', 'Hall A'),
('Workshop Space', 250.00, 40, (SELECT id FROM address WHERE address_line_1 = '147 Pelham Street' AND address_line_2 = 'Suite 5' AND city = 'Carlton'), '1st Floor', 'Room 102'),
('Study Area', 50.00, 25, (SELECT id FROM address WHERE address_line_1 = '258 Drummond Street' AND address_line_2 IS NULL AND city = 'Carlton'), '2nd Floor', 'Room 202'),
('Collaborative Space', 175.00, 35, (SELECT id FROM address WHERE address_line_1 = '369 Rathdowne Street' AND address_line_2 = 'Unit 3' AND city = 'Carlton'), '3rd Floor', 'Room 302'),
('Meeting Room', 125.00, 15, (SELECT id FROM address WHERE address_line_1 = '741 Elgin Street' AND address_line_2 IS NULL AND city = 'Carlton'), '4th Floor', 'Room 402'),
('Main Hall', 800.00, 300, (SELECT id FROM address WHERE address_line_1 = '852 Queensberry Street' AND address_line_2 = 'Room 202' AND city = 'Carlton'), 'Ground Floor', 'Hall B'),
('Classroom', 150.00, 40, (SELECT id FROM address WHERE address_line_1 = '963 Bouverie Street' AND address_line_2 IS NULL AND city = 'Carlton'), '1st Floor', 'Room 103'),
('Lab Space', 225.00, 30, (SELECT id FROM address WHERE address_line_1 = '159 Berkeley Street' AND address_line_2 = 'Building B' AND city = 'Carlton'), '2nd Floor', 'Lab 201'),
('Discussion Room', 100.00, 20, (SELECT id FROM address WHERE address_line_1 = '753 Faraday Street' AND address_line_2 IS NULL AND city = 'Carlton'), '3rd Floor', 'Room 303'),
('Presentation Room', 200.00, 50, (SELECT id FROM address WHERE address_line_1 = '951 Cardigan Street' AND address_line_2 = 'Level 3' AND city = 'Carlton'), '4th Floor', 'Room 403'),
('Outdoor Amphitheatre', 400.00, 100, (SELECT id FROM address WHERE address_line_1 = '357 Swanston Street' AND address_line_2 IS NULL AND city = 'Melbourne'), 'Ground Floor', 'Amphitheatre'),
('Indoor Sports Court', 300.00, 60, (SELECT id FROM address WHERE address_line_1 = '864 Russell Street' AND address_line_2 = 'Room 303' AND city = 'Melbourne'), '1st Floor', 'Court 1'),
('Art Studio', 175.00, 25, (SELECT id FROM address WHERE address_line_1 = '246 Exhibition Street' AND address_line_2 IS NULL AND city = 'Melbourne'), '2nd Floor', 'Studio 201'),
('Music Room', 150.00, 20, (SELECT id FROM address WHERE address_line_1 = '135 LaTrobe Street' AND address_line_2 = 'Suite 7' AND city = 'Melbourne'), '3rd Floor', 'Room 304'),
('Dance Studio', 200.00, 30, (SELECT id FROM address WHERE address_line_1 = '579 Lonsdale Street' AND address_line_2 IS NULL AND city = 'Melbourne'), '4th Floor', 'Studio 401'),
('Photography Studio', 225.00, 15, (SELECT id FROM address WHERE address_line_1 = '468 Little Collins Street' AND address_line_2 = 'Unit 4' AND city = 'Melbourne'), '1st Floor', 'Studio 101'),
('Film Screening Room', 250.00, 40, (SELECT id FROM address WHERE address_line_1 = '792 Flinders Street' AND address_line_2 IS NULL AND city = 'Melbourne'), '2nd Floor', 'Screen 1'),
('Language Lab', 175.00, 25, (SELECT id FROM address WHERE address_line_1 = '513 Spencer Street' AND address_line_2 = 'Room 404' AND city = 'Melbourne'), '3rd Floor', 'Lab 302'),
('Debate Chamber', 150.00, 30, (SELECT id FROM address WHERE address_line_1 = '624 King Street' AND address_line_2 IS NULL AND city = 'West Melbourne'), '4th Floor', 'Chamber 1'),
('Science Lab', 275.00, 35, (SELECT id FROM address WHERE address_line_1 = '735 Dryburgh Street' AND address_line_2 = 'Building C' AND city = 'North Melbourne'), '1st Floor', 'Lab 102'),
('Engineering Workshop', 300.00, 40, (SELECT id FROM address WHERE address_line_1 = '846 Abbotsford Street' AND address_line_2 IS NULL AND city = 'North Melbourne'), '2nd Floor', 'Workshop 201'),
('Psychology Testing Room', 200.00, 10, (SELECT id FROM address WHERE address_line_1 = '957 Courtney Street' AND address_line_2 = 'Level 4' AND city = 'North Melbourne'), '3rd Floor', 'Room 305'),
('Moot Court', 225.00, 50, (SELECT id FROM address WHERE address_line_1 = '168 Flemington Road' AND address_line_2 IS NULL AND city = 'North Melbourne'), '4th Floor', 'Court Room'),
('Astronomy Observatory', 350.00, 20, (SELECT id FROM address WHERE address_line_1 = '279 Peel Street' AND address_line_2 = 'Suite 9' AND city = 'North Melbourne'), '5th Floor', 'Observatory'),
('Geology Field Station', 275.00, 30, (SELECT id FROM address WHERE address_line_1 = '381 Victoria Street' AND address_line_2 IS NULL AND city = 'North Melbourne'), 'Ground Floor', 'Field Station');

-- Insert data into online_venue table (30 rows)
INSERT INTO online_venue (description, cost, venue_capacity, link)
VALUES 
('Zoom Webinar', 100.00, 500, 'https://unimelb.zoom.us/j/1234567890'),
('Google Meet', 0.00, 100, 'https://meet.google.com/abc-defg-hij'),
('Microsoft Teams', 50.00, 300, 'https://teams.microsoft.com/l/meetup-join/1234567890'),
('Cisco Webex', 75.00, 200, 'https://unimelb.webex.com/meet/room1'),
('BigBlueButton', 25.00, 150, 'https://bbb.unimelb.edu.au/gl/abc-def-ghi'),
('Adobe Connect', 80.00, 250, 'https://unimelb.adobeconnect.com/room1'),
('GoToWebinar', 90.00, 400, 'https://attendee.gotowebinar.com/register/1234567890'),
('Blackboard Collaborate', 60.00, 180, 'https://us.bbcollab.com/guest/abcdefghij'),
('Skype for Business', 40.00, 120, 'https://meet.lync.com/unimelb/username/ABCDEF'),
('Jitsi Meet', 0.00, 75, 'https://meet.jit.si/UniMelbRoom1'),
('YouTube Live', 20.00, 1000, 'https://youtu.be/abcdefghijk'),
('Facebook Live', 15.00, 800, 'https://www.facebook.com/UniMelb/live/'),
('Twitch Stream', 30.00, 500, 'https://www.twitch.tv/unimelb'),
('Vimeo Livestream', 70.00, 300, 'https://vimeo.com/event/123456'),
('Panopto Webcast', 55.00, 200, 'https://unimelb.hosted.panopto.com/1234'),
('ON24 Webcast', 85.00, 450, 'https://event.on24.com/wcc/r/1234567/ABCDEFG'),
('BlueJeans Events', 65.00, 350, 'https://primetime.bluejeans.com/a2m/live-event/abcdefg'),
('Whereby', 35.00, 100, 'https://whereby.com/unimelb-room1'),
('Livestorm', 75.00, 250, 'https://app.livestorm.co/unimelb/event1'),
('StreamYard', 50.00, 150, 'https://streamyard.com/abcdefghij'),
('Demio', 80.00, 200, 'https://event.demio.com/join/abcdefghij'),
('Crowdcast', 70.00, 300, 'https://www.crowdcast.io/e/unimelb-event1'),
('Remo', 90.00, 400, 'https://live.remo.co/e/unimelb-event'),
('Hopin', 100.00, 500, 'https://hopin.to/events/unimelb-event1'),
('RunTheWorld', 85.00, 350, 'https://www.runtheworld.today/app/c/unimelb'),
('Airmeet', 75.00, 300, 'https://www.airmeet.com/e/abcdef12-3456-7890-abcd-efghijklmnop'),
('HeySummit', 60.00, 250, 'https://unimelb.heysummit.com/'),
('Accelevents', 95.00, 450, 'https://www.accelevents.com/e/unimelb'),
('vFairs', 110.00, 600, 'https://unimelb.vfairs.com/'),
('6Connex', 105.00, 550, 'https://unimelb.6connex.com/event/virtual');

-- Insert data into event table (at least 30 rows) with venue IDs obtained from select queries
INSERT INTO event (club_id, is_online, is_cancelled, physical_venue_id, online_venue_id, name, description, start_time, end_time, cost, num_tickets, event_capacity)
VALUES 
((SELECT id FROM club WHERE name = 'Computer Science Club'), false, false, (SELECT id FROM physical_venue WHERE description = 'Large Auditorium'), NULL, 'Annual Hackathon', 'Join us for a 24-hour coding challenge!', '2024-10-15 09:00:00', '2024-10-16 09:00:00', 0.00, 15, 200),
((SELECT id FROM club WHERE name = 'Physics Society'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://unimelb.zoom.us/j/1234567890'), 'Quantum Computing Seminar', 'Learn about the latest advancements in quantum computing', '2024-09-20 15:00:00', '2024-09-20 17:00:00', 5.00, 15, 500),
((SELECT id FROM club WHERE name = 'Mathematics Association'), false, false, (SELECT id FROM physical_venue WHERE description = 'Lecture Theatre'), NULL, 'Pi Day Celebration', 'Come celebrate Pi Day with fun math activities!', '2024-03-14 14:00:00', '2024-03-14 16:00:00', 3.14, 15, 150),
((SELECT id FROM club WHERE name = 'Chemistry Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://meet.google.com/abc-defg-hij'), 'Virtual Lab Tour', 'Take a virtual tour of our state-of-the-art chemistry labs', '2024-11-05 13:00:00', '2024-11-05 14:30:00', 0.00, 0, 100),
((SELECT id FROM club WHERE name = 'Biology Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Seminar Room'), NULL, 'Biodiversity Workshop', 'Hands-on workshop exploring local biodiversity', '2024-04-22 10:00:00', '2024-04-22 15:00:00', 10.00, 0, 30),
((SELECT id FROM club WHERE name = 'Engineering Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://teams.microsoft.com/l/meetup-join/1234567890'), 'Future of Robotics Panel', 'Expert panel discussion on the future of robotics', '2024-08-10 18:00:00', '2024-08-10 20:00:00', 7.50, 0, 300),
((SELECT id FROM club WHERE name = 'Psychology Students Association'), false, false, (SELECT id FROM physical_venue WHERE description = 'Workshop Space'), NULL, 'Mental Health Awareness Day', 'Workshops and talks promoting mental health awareness', '2024-05-10 09:00:00', '2024-05-10 17:00:00', 0.00, 0, 40),
((SELECT id FROM club WHERE name = 'Economics Society'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://unimelb.webex.com/meet/room1'), 'Cryptocurrency Debate', 'Debate on the future of cryptocurrency in the global economy', '2024-07-15 19:00:00', '2024-07-15 21:00:00', 5.00, 0, 200),
((SELECT id FROM club WHERE name = 'Law Students Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Collaborative Space'), NULL, 'Mock Trial Competition', 'Annual mock trial competition for law students', '2024-09-01 10:00:00', '2024-09-01 18:00:00', 15.00, 0, 35),
((SELECT id FROM club WHERE name = 'History Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://bbb.unimelb.edu.au/gl/abc-def-ghi'), 'Virtual Museum Tour', 'Guided virtual tour of ancient civilizations exhibits', '2024-06-20 14:00:00', '2024-06-20 16:00:00', 3.00, 0, 150),
((SELECT id FROM club WHERE name = 'English Literature Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Main Hall'), NULL, 'Shakespeare in the Park', 'Outdoor performance of A Midsummer Night''s Dream', '2024-02-14 19:00:00', '2024-02-14 22:00:00', 5.00, 0, 300),
((SELECT id FROM club WHERE name = 'Medical Students Association'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://unimelb.adobeconnect.com/room1'), 'Telemedicine Symposium', 'Exploring the future of telemedicine', '2024-11-30 09:00:00', '2024-11-30 17:00:00', 20.00, 0, 250),
((SELECT id FROM club WHERE name = 'Sociology Club'), false, false, (SELECT id FROM physical_venue WHERE description = 'Lab Space'), NULL, 'Urban Sociology Fieldtrip', 'Exploring urban dynamics in Melbourne', '2024-04-05 10:00:00', '2024-04-05 15:00:00', 8.00, 0, 30),
((SELECT id FROM club WHERE name = 'Art History Society'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://attendee.gotowebinar.com/register/1234567890'), 'Virtual Gallery Opening', 'Online exhibition of student artworks', '2024-10-01 18:00:00', '2024-10-01 20:00:00', 0.00, 0, 400),
((SELECT id FROM club WHERE name = 'Political Science Association'), false, false, (SELECT id FROM physical_venue WHERE description = 'Presentation Room'), NULL, 'Model UN Conference', 'Annual Model United Nations conference', '2024-07-05 09:00:00', '2024-07-07 17:00:00', 30.00, 0, 50),
((SELECT id FROM club WHERE name = 'Environmental Science Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://us.bbcollab.com/guest/abcdefghij'), 'Climate Change Webinar', 'Expert panel on climate change mitigation', '2024-04-22 14:00:00', '2024-04-22 16:00:00', 5.00, 0, 180),
((SELECT id FROM club WHERE name = 'Music Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Indoor Sports Court'), NULL, 'Battle of the Bands', 'Annual music competition', '2024-05-20 18:00:00', '2024-05-20 23:00:00', 10.00, 0, 60),
((SELECT id FROM club WHERE name = 'Anthropology Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://meet.lync.com/unimelb/username/ABCDEF'), 'Cultural Diversity Day', 'Virtual celebration of cultural diversity', '2024-09-21 11:00:00', '2024-09-21 15:00:00', 0.00, 0, 120),
((SELECT id FROM club WHERE name = 'Philosophy Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Music Room'), NULL, 'Ethics Debate', 'Debate on contemporary ethical issues', '2024-03-15 16:00:00', '2024-03-15 18:00:00', 3.00, 0, 20),
((SELECT id FROM club WHERE name = 'Linguistics Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://meet.jit.si/UniMelbRoom1'), 'Language Exchange Meet', 'Virtual language exchange event', '2024-06-10 19:00:00', '2024-06-10 21:00:00', 0.00, 0, 75),
((SELECT id FROM club WHERE name = 'Astronomy Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Photography Studio'), NULL, 'Stargazing Night', 'Observing celestial bodies', '2024-08-12 21:00:00', '2024-08-13 01:00:00', 5.00, 0, 15),
((SELECT id FROM club WHERE name = 'Geography Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://youtu.be/abcdefghijk'), 'GIS Workshop', 'Introduction to Geographic Information Systems', '2024-10-05 10:00:00', '2024-10-05 12:00:00', 7.00, 0, 200),
((SELECT id FROM club WHERE name = 'Statistics Association'), false, false, (SELECT id FROM physical_venue WHERE description = 'Language Lab'), NULL, 'Data Science Hackathon', '24-hour data science challenge', '2024-11-15 09:00:00', '2024-11-16 09:00:00', 15.00, 0, 30),
((SELECT id FROM club WHERE name = 'Biochemistry Society'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://www.facebook.com/UniMelb/live/'), 'Protein Folding Seminar', 'Latest research in protein folding', '2024-04-18 15:00:00', '2024-04-18 17:00:00', 5.00, 0, 200),
((SELECT id FROM club WHERE name = 'Archaeology Club'), false, false, (SELECT id FROM physical_venue WHERE description = 'Science Lab'), NULL, 'Dig Site Simulation', 'Hands-on archaeology experience', '2024-09-08 09:00:00', '2024-09-08 16:00:00', 20.00, 0, 35),
((SELECT id FROM club WHERE name = 'Media Studies Society'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://www.twitch.tv/unimelb'), 'Digital Media Symposium', 'Exploring the impact of digital media', '2024-07-22 13:00:00', '2024-07-22 17:00:00', 8.00, 0, 150),
((SELECT id FROM club WHERE name = 'Neuroscience Club'), false, false, (SELECT id FROM physical_venue WHERE description = 'Psychology Testing Room'), NULL, 'Brain Awareness Week', 'Series of talks on brain function and health', '2024-03-11 10:00:00', '2024-03-15 16:00:00', 0.00, 0, 10),
((SELECT id FROM club WHERE name = 'Education Students Association'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://vimeo.com/event/123456'), 'Future of Education Forum', 'Discussion on emerging trends in education', '2024-05-30 14:00:00', '2024-05-30 16:00:00', 5.00, 0, 300),
((SELECT id FROM club WHERE name = 'Film Society'), false, false, (SELECT id FROM physical_venue WHERE description = 'Astronomy Observatory'), NULL, 'Student Film Festival', 'Showcasing student-made short films', '2024-10-20 18:00:00', '2024-10-20 22:00:00', 10.00, 0, 20),
((SELECT id FROM club WHERE name = 'Geology Club'), true, false, NULL, (SELECT id FROM online_venue WHERE link = 'https://unimelb.hosted.panopto.com/1234'), 'Virtual Rock Identification Workshop', 'Learn to identify common rock types', '2024-04-12 11:00:00', '2024-04-12 13:00:00', 3.00, 0, 200);
-- Insert data into funding_application table (at least 30 rows)
INSERT INTO funding_application (club_id, description, amount, status, submitted_at, semester, year)
VALUES 
((SELECT id FROM club WHERE name = 'Computer Science Club'), 'Funding for hackathon prizes', 1000.00, 'Submitted', NOW(), 1, 2024),
((SELECT id FROM club WHERE name = 'Physics Society'), 'Equipment for quantum computing demo', 1500.00, 'Approved', '2024-08-15', 2, 2024),
((SELECT id FROM club WHERE name = 'Mathematics Association'), 'Pi Day celebration supplies', 300.00, 'In Review', '2024-02-20', 1, 2024),
((SELECT id FROM club WHERE name = 'Chemistry Club'), 'Virtual lab tour software license', 500.00, 'Rejected', '2024-10-01', 1, 2024),
((SELECT id FROM club WHERE name = 'Biology Society'), 'Biodiversity workshop materials', 750.00, 'Approved', '2024-03-15', 2, 2023),
((SELECT id FROM club WHERE name = 'Engineering Club'), 'Robotics panel guest speaker fees', 2000.00, 'Submitted', '2024-07-01', 2, 2024),
((SELECT id FROM club WHERE name = 'Psychology Students Association'), 'Mental health awareness day promotional materials', 400.00, 'In Review', '2024-04-10', 1, 2024),
((SELECT id FROM club WHERE name = 'Economics Society'), 'Cryptocurrency debate venue rental', 600.00, 'Approved', '2024-06-15', 2, 2024),
((SELECT id FROM club WHERE name = 'Law Students Society'), 'Mock trial competition prizes', 1000.00, 'Submitted', '2024-08-01', 2, 2024),
((SELECT id FROM club WHERE name = 'History Club'), 'Virtual museum tour software subscription', 350.00, 'In Draft', '2024-05-20', 1, 2024),
((SELECT id FROM club WHERE name = 'English Literature Society'), 'Shakespeare in the Park costume rentals', 800.00, 'Approved', NOW(), 1, 2024),
((SELECT id FROM club WHERE name = 'Medical Students Association'), 'Telemedicine symposium guest speaker travel', 2500.00, 'In Review', '2024-10-30', 1, 2025),
((SELECT id FROM club WHERE name = 'Sociology Club'), 'Urban sociology fieldtrip transportation', 450.00, 'Submitted', '2024-03-05', 2, 2023),
((SELECT id FROM club WHERE name = 'Art History Society'), 'Virtual gallery hosting fees', 200.00, 'Approved', '2024-09-01', 1, 2024),
((SELECT id FROM club WHERE name = 'Political Science Association'), 'Model UN conference materials', 1200.00, 'In Draft', '2024-06-05', 2, 2024),
((SELECT id FROM club WHERE name = 'Environmental Science Club'), 'Climate change webinar platform subscription', 300.00, 'Submitted', '2024-03-22', 2, 2023),
((SELECT id FROM club WHERE name = 'Music Society'), 'Battle of the Bands sound equipment rental', 1500.00, 'Approved', '2024-04-20', 1, 2024),
((SELECT id FROM club WHERE name = 'Anthropology Club'), 'Cultural Diversity Day virtual event platform', 250.00, 'In Review', '2024-08-21', 2, 2024),
((SELECT id FROM club WHERE name = 'Philosophy Society'), 'Ethics debate promotional materials', 150.00, 'Rejected', '2024-02-15', 1, 2024),
((SELECT id FROM club WHERE name = 'Linguistics Club'), 'Language exchange software license', 400.00, 'Submitted', '2024-05-10', 1, 2024),
((SELECT id FROM club WHERE name = 'Astronomy Society'), 'Telescope maintenance and upgrades', 2000.00, 'Approved', '2024-07-12', 2, 2024),
((SELECT id FROM club WHERE name = 'Geography Club'), 'GIS workshop software licenses', 1000.00, 'In Review', '2024-09-05', 1, 2024),
((SELECT id FROM club WHERE name = 'Statistics Association'), 'Data Science Hackathon prizes', 1500.00, 'Submitted', '2024-10-15', 1, 2025),
((SELECT id FROM club WHERE name = 'Biochemistry Society'), 'Protein folding seminar guest speaker honorarium', 800.00, 'Approved', '2024-03-18', 2, 2023),
((SELECT id FROM club WHERE name = 'Archaeology Club'), 'Dig site simulation equipment', 1200.00, 'In Draft', '2024-08-08', 2, 2024),
((SELECT id FROM club WHERE name = 'Media Studies Society'), 'Digital media symposium online platform', 500.00, 'Submitted', '2024-06-22', 2, 2024),
((SELECT id FROM club WHERE name = 'Neuroscience Club'), 'Brain Awareness Week promotional materials', 300.00, 'In Review', '2024-02-11', 1, 2024),
((SELECT id FROM club WHERE name = 'Education Students Association'), 'Future of Education forum guest panel expenses', 1000.00, 'Approved', '2024-04-30', 1, 2024),
((SELECT id FROM club WHERE name = 'Film Society'), 'Student Film Festival venue rental and equipment', 1800.00, 'Submitted', '2024-09-20', 1, 2024),
((SELECT id FROM club WHERE name = 'Geology Club'), 'Virtual rock identification workshop materials', 400.00, 'In Draft', '2024-03-12', 2, 2023);

-- Insert data into review table (at least 10 entries)
INSERT INTO review (faculty_id, application_id, review_start_date, decision, comments)
VALUES 
((SELECT id FROM faculty_member WHERE username = 'johndoe'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Computer Science Club')), '2024-09-15', 'Approved', 'Well-planned event with clear budget allocation.'),
((SELECT id FROM faculty_member WHERE username = 'janesmith'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Physics Society')), '2024-08-20', 'Approved', 'Excellent initiative to promote quantum computing awareness.'),
((SELECT id FROM faculty_member WHERE username = 'alexwong'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Mathematics Association')), '2024-02-25', 'Approved', 'Creative idea for Pi Day celebration.'),
((SELECT id FROM faculty_member WHERE username = 'sarahj'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Chemistry Club')), '2024-10-10', 'Rejected', 'Budget seems excessive for virtual tour software.'),
((SELECT id FROM faculty_member WHERE username = 'michaelb'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Biology Society')), '2024-03-20', 'Approved', 'Important initiative for biodiversity awareness.'),
((SELECT id FROM faculty_member WHERE username = 'emilyt'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Engineering Club')), '2024-07-10', 'Approved', 'High-profile speakers justify the budget.'),
((SELECT id FROM faculty_member WHERE username = 'davidw'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Psychology Students Association')), '2024-04-15', 'Approved', 'Critical initiative for student well-being.'),
((SELECT id FROM faculty_member WHERE username = 'lisac'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Economics Society')), '2024-06-20', 'Approved', 'Timely topic for debate, reasonable budget.'),
((SELECT id FROM faculty_member WHERE username = 'robertm'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'Law Students Society')), '2024-08-10', 'Approved', 'Valuable practical experience for law students.'),
((SELECT id FROM faculty_member WHERE username = 'amandag'), (SELECT id FROM funding_application WHERE club_id = (SELECT id FROM club WHERE name = 'History Club')), '2024-05-25', 'Approved', 'Innovative use of technology for historical education.');

INSERT INTO RSVP (RSVP_student_id, event_id, date_created)
VALUES 
((SELECT id FROM student WHERE username = 'alicej'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-01'),
((SELECT id FROM student WHERE username = 'bobw'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-02'),
((SELECT id FROM student WHERE username = 'charlieb'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-03'),
((SELECT id FROM student WHERE username = 'dianal'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-04'),
((SELECT id FROM student WHERE username = 'ethann'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-05'),
((SELECT id FROM student WHERE username = 'fionas'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-06'),
((SELECT id FROM student WHERE username = 'georgep'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-07'),
((SELECT id FROM student WHERE username = 'hannahk'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-08'),
((SELECT id FROM student WHERE username = 'ianc'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-09'),
((SELECT id FROM student WHERE username = 'juliag'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), '2024-09-10'),

-- Quantum Computing Seminar RSVPs
((SELECT id FROM student WHERE username = 'kevint'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-15'),
((SELECT id FROM student WHERE username = 'lauraw'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-16'),
((SELECT id FROM student WHERE username = 'michaelo'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-17'),
((SELECT id FROM student WHERE username = 'natalies'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-18'),
((SELECT id FROM student WHERE username = 'oliverb'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-19'),
((SELECT id FROM student WHERE username = 'patricial'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-20'),
((SELECT id FROM student WHERE username = 'quinnm'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-21'),
((SELECT id FROM student WHERE username = 'ryann'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-22'),
((SELECT id FROM student WHERE username = 'sophiac'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-23'),
((SELECT id FROM student WHERE username = 'thomasw'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), '2024-08-24'),

-- Pi Day Celebration RSVPs
((SELECT id FROM student WHERE username = 'ursulak'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-20'),
((SELECT id FROM student WHERE username = 'victorp'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-21'),
((SELECT id FROM student WHERE username = 'wendyz'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-22'),
((SELECT id FROM student WHERE username = 'xavierg'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-23'),
((SELECT id FROM student WHERE username = 'yasmint'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-24'),
((SELECT id FROM student WHERE username = 'zackw'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-25'),
((SELECT id FROM student WHERE username = 'amyo'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-26'),
((SELECT id FROM student WHERE username = 'bens'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-27'),
((SELECT id FROM student WHERE username = 'chloeb'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-28'),
((SELECT id FROM student WHERE username = 'daniell'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), '2024-02-29');


-- Insert tickets for Annual Hackathon
INSERT INTO ticket (RSVP_student_id, event_id, ticket_student_id, special_preferences)
VALUES 
-- Primary tickets
((SELECT id FROM student WHERE username = 'alicej'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'alicej'), NULL),
((SELECT id FROM student WHERE username = 'bobw'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'bobw'), NULL),
((SELECT id FROM student WHERE username = 'charlieb'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'charlieb'), NULL),
((SELECT id FROM student WHERE username = 'dianal'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'dianal'), NULL),
((SELECT id FROM student WHERE username = 'ethann'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'ethann'), NULL),
-- Guest tickets (two per RSVP)
((SELECT id FROM student WHERE username = 'alicej'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'fionas'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'alicej'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'georgep'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'bobw'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'hannahk'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'bobw'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'ianc'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'charlieb'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'juliag'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'charlieb'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'kevint'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'dianal'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'lauraw'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'dianal'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'michaelo'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'ethann'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'natalies'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'ethann'), (SELECT id FROM event WHERE name = 'Annual Hackathon'), (SELECT id FROM student WHERE username = 'oliverb'), 'Guest 2');

-- Insert tickets for Quantum Computing Seminar
INSERT INTO ticket (RSVP_student_id, event_id, ticket_student_id, special_preferences)
VALUES 
-- Primary tickets
((SELECT id FROM student WHERE username = 'kevint'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'kevint'), NULL),
((SELECT id FROM student WHERE username = 'lauraw'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'lauraw'), NULL),
((SELECT id FROM student WHERE username = 'michaelo'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'michaelo'), NULL),
((SELECT id FROM student WHERE username = 'natalies'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'natalies'), NULL),
((SELECT id FROM student WHERE username = 'oliverb'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'oliverb'), NULL),
-- Guest tickets (two per RSVP)
((SELECT id FROM student WHERE username = 'kevint'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'patricial'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'kevint'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'quinnm'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'lauraw'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'ryann'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'lauraw'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'sophiac'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'michaelo'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'thomasw'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'michaelo'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'ursulak'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'natalies'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'victorp'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'natalies'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'wendyz'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'oliverb'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'xavierg'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'oliverb'), (SELECT id FROM event WHERE name = 'Quantum Computing Seminar'), (SELECT id FROM student WHERE username = 'yasmint'), 'Guest 2');

-- Insert tickets for Pi Day Celebration
INSERT INTO ticket (RSVP_student_id, event_id, ticket_student_id, special_preferences)
VALUES 
-- Primary tickets
((SELECT id FROM student WHERE username = 'ursulak'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'ursulak'), NULL),
((SELECT id FROM student WHERE username = 'victorp'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'victorp'), NULL),
((SELECT id FROM student WHERE username = 'wendyz'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'wendyz'), NULL),
((SELECT id FROM student WHERE username = 'xavierg'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'xavierg'), NULL),
((SELECT id FROM student WHERE username = 'yasmint'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'yasmint'), NULL),
-- Guest tickets (two per RSVP)
((SELECT id FROM student WHERE username = 'ursulak'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'zackw'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'ursulak'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'amyo'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'victorp'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'bens'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'victorp'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'chloeb'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'wendyz'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'daniell'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'wendyz'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'alicej'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'xavierg'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'bobw'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'xavierg'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'charlieb'), 'Guest 2'),
((SELECT id FROM student WHERE username = 'yasmint'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'dianal'), 'Guest 1'),
((SELECT id FROM student WHERE username = 'yasmint'), (SELECT id FROM event WHERE name = 'Pi Day Celebration'), (SELECT id FROM student WHERE username = 'ethann'), 'Guest 2');

