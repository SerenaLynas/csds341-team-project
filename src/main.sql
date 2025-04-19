USE CampaignManager;

-- https://app.diagrams.net/#G1QiWZ3wq5PZXeAbmcHHWDaDdaCLKeV3u0#%7B%22pageId%22%3A%22NwJbyu9zbr4dZljTRoKy%22%7D

DROP TABLE IF EXISTS person_issue;
DROP TABLE IF EXISTS attend_event;
DROP TABLE IF EXISTS vote_cast;
DROP TABLE IF EXISTS issue;
DROP TABLE IF EXISTS donation;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS campaign;
DROP TABLE IF EXISTS election;
DROP TABLE IF EXISTS venue;
DROP TABLE IF EXISTS person;

CREATE TABLE person (
    person_id INTEGER IDENTITY PRIMARY KEY,
    first VARCHAR(255) NOT NULL,
    last VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    phone VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    district VARCHAR(5)
);

CREATE TABLE venue (
    venue_id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    max_capacity INTEGER
);

CREATE TABLE election (
    election_id INTEGER IDENTITY PRIMARY KEY,
    registration_deadline DATE NOT NULL,
    date DATE NOT NULL
);

CREATE TABLE campaign (
    campaign_id INTEGER IDENTITY PRIMARY KEY,
    candidate_id INTEGER NOT NULL,
    manager_id INTEGER NOT NULL,
    election_id INTEGER NOT NULL,
    funds NUMERIC DEFAULT 0,
    FOREIGN KEY (candidate_id) REFERENCES person(person_id),
    FOREIGN KEY (manager_id) REFERENCES person(person_id),
    FOREIGN KEY (election_id) REFERENCES election(election_id)
);

DROP TABLE IF EXISTS event;
CREATE TABLE event (
    event_id INTEGER IDENTITY PRIMARY KEY,
    venue_id INTEGER,
    campaign_id INTEGER NOT NULL,
    max_capacity INTEGER,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    time_start DATETIME2 NOT NULL,
    time_end DATETIME2 NOT NULL,
    type VARCHAR(255) CHECK (type IN ('town hall', 'gotv', 'rally', 'phone bank', 'other')),
    FOREIGN KEY (venue_id) REFERENCES venue(venue_id),
    FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)
);

DROP TABLE IF EXISTS donation;
CREATE TABLE donation (
    donation_id INTEGER IDENTITY PRIMARY KEY,
    person_id INTEGER NOT NULL,
    campaign_id INTEGER NOT NULL,
    amount NUMERIC NOT NULL,
    FOREIGN KEY (person_id) REFERENCES person(person_id),
    FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)
);

DROP TABLE IF EXISTS issue;
CREATE TABLE issue (
    issue_id INTEGER IDENTITY PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS vote_cast;
CREATE TABLE vote_cast (
    person_id INTEGER NOT NULL,
    election_id INTEGER NOT NULL,
    type VARCHAR(255) NOT NULL,
    timestamp DATETIME2 NOT NULL,
    voted_for INTEGER,
    PRIMARY KEY (person_id, election_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id),
    FOREIGN KEY (election_id) REFERENCES election(election_id),
    FOREIGN KEY (voted_for) REFERENCES campaign(campaign_id)
);

DROP TABLE IF EXISTS attend_event;
CREATE TABLE attend_event (
    event_id INTEGER NOT NULL,
    person_id INTEGER NOT NULL,
    register_utm VARCHAR(255),
    register_timestamp DATETIME2 DEFAULT (SYSUTCDATETIME()) NOT NULL,
    PRIMARY KEY (event_id, person_id),
    FOREIGN KEY (event_id) REFERENCES event(event_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);

DROP TABLE IF EXISTS person_issue;
CREATE TABLE person_issue (
    person_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    PRIMARY KEY (person_id, issue_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id),
    FOREIGN KEY (issue_id) REFERENCES issue(issue_id)
);

INSERT INTO person (first, last, dob, phone, email, address, district) VALUES
('John', 'James', '1985-04-12', '555-1234', 'alice.johnson@example.com', '123 Euclid Ave, Cleveland', 'OH-99'),
('Bob', 'Bobby', '1978-11-23', '555-2345', 'bob.smith@example.com', '456 Elm St, Cleveland', 'OH-57'),
('Ann', 'Annie', '1990-01-08', '555-3456', 'carol.white@example.com', '789 Oak St, Cleveland', 'OH-23'),
('David', 'Brown', '1967-05-17', '555-4567', 'david.brown@example.com', '101 Maple Ave, Cleveland', 'OH-65'),
('Eve', 'Davis', '1983-03-04', '555-5678', 'eve.davis@example.com', '202 Birch Rd, Cleveland', 'OH-57'),
('Frank', 'Moore', '1992-09-22', '555-6789', 'frank.moore@example.com', '303 Cedar Blvd, Cleveland', 'OH-99'),
('Grace', 'Walker', '1975-07-15', '555-7890', 'grace.clark@example.com', '404 Pine Cir, Cleveland', 'OH-65'),
('Hank', 'Miller', '1980-02-28', '555-8901', 'hank.miller@example.com', '505 Cherry Ln, Cleveland', 'OH-57'),
('Ivy', 'Wilson', '1995-12-09', '555-9012', 'ivy.wilson@example.com', '606 Euclid Ave, Cleveland', 'OH-57'),
('Jack', 'Taylor', '1988-06-30', '555-0123', 'jack.taylor@example.com', '707 Euclid Ave, Cleveland', 'OH-99'),
('Karen', 'Anderson', '1972-08-19', '555-1023', 'karen.anderson@example.com', '808 Hemlock Way, Cleveland', 'OH-65'),
('Leo', 'Thomas', '1965-10-05', '555-1123', 'leo.thomas@example.com', '909 Poplar St, Cleveland', 'OH-65'),
('Mia', 'Jackson', '1993-11-27', '555-1223', 'mia.jackson@example.com', '121 Ivy Rd, Cleveland', 'OH-57'),
('Nick', 'Harris', '1986-02-14', '555-1323', 'nick.harris@example.com', '131 Fir St, Cleveland', 'OH-57'),
('Olivia', 'Martin', '1979-04-07', '555-1423', 'olivia.martin@example.com', '141 Oakwood Ave, Cleveland', 'OH-65'),
('Paul', 'Lee', '1991-03-12', '555-1523', 'paul.lee@example.com', '151 Brookside Dr, Cleveland', 'OH-57'),
('Quinn', 'Walker', '1982-07-21', '555-1623', 'quinn.walker@example.com', '161 Valley Rd, Cleveland', 'OH-99'),
('Rachel', 'Hall', '1994-09-13', '555-1723', 'rachel.hall@example.com', '171 Hillside Blvd, Cleveland', 'OH-57'),
('Sam', 'Allen', '1977-01-29', '555-1823', 'sam.allen@example.com', '181 Canyon St, Cleveland', 'OH-57'),
('Tina', 'Young', '1989-12-25', '555-1923', 'tina.young@example.com', '191 Meadow Ln, Cleveland', 'OH-57');

INSERT INTO venue (name, location, max_capacity) VALUES
('Cleveland Community Center', 'Northwood', 300),
('Eastfield Town Hall', 'Eastfield', 500),
('Westhaven Civic Arena', 'Westhaven', 750),
('Southend Park Pavilion', 'Southend', 200),
('Northwood Conference Hall', 'Northwood', 600);

INSERT INTO election (registration_deadline, date) VALUES
('2025-05-01', '2025-06-01'),
('2025-10-01', '2025-11-01');

INSERT INTO campaign (candidate_id, manager_id, election_id, funds) VALUES
(1, 11, 1, 50000),
(2, 12, 1, 30000),
(3, 13, 1, 42000),
(4, 14, 1, 27000),
(5, 15, 1, 61000),
(6, 16, 2, 39000),
(7, 17, 2, 50000),
(8, 18, 2, 32000),
(9, 19, 2, 47000),
(10, 20, 2, 38000);

INSERT INTO event (campaign_id, max_capacity, name, description, time_start, time_end, type)
VALUES 
(1, 300, 'Northwood Kickoff', 'Kickoff rally in Northwood', '2026-04-25 00:00:00', '2026-04-25 01:00:00', 'rally'),
(2, 500, 'Eastfield Open Mic', 'Open mic with candidate', '2026-04-26 00:00:00', '2026-04-26 01:00:00', 'town hall'),
(3, 750, 'Door knocking', 'Door knocking', '2026-04-27 00:00:00', '2026-04-27 01:00:00', 'gotv'),
(4, 200, 'Southend Strategy Night', 'Meet & greet with campaign team', '2026-04-28 00:00:00', '2026-04-28 01:00:00', 'other'),
(5, 600, 'Phone bank', 'Volunteers call voters', '2026-04-29 00:00:00', '2026-04-29 01:00:00', 'phone bank'),
(1, 300, 'Healthcare Town Hall', 'Focused on healthcare reform', '2026-04-30 00:00:00', '2026-04-30 01:00:00', 'town hall'),
(2, 500, 'Education Reform Rally', 'Speech on education system', '2026-05-01 00:00:00', '2026-05-01 01:00:00', 'rally'),
(3, 750, 'Door knocking', 'Door knocking', '2026-05-02 00:00:00', '2026-05-02 01:00:00', 'gotv'),
(4, 200, 'Community Q&A', 'Answering local questions', '2026-05-03 00:00:00', '2026-05-03 01:00:00', 'town hall'),
(5, 600, 'Phone Banking Finale', 'Final call push before election', '2026-05-04 00:00:00', '2026-05-04 01:00:00', 'phone bank');

INSERT INTO donation (person_id, campaign_id, amount) VALUES
(11, 1, 25000),
(12, 2, 10000),
(13, 3, 30000),
(14, 4, 20000),
(15, 5, 50000),
(16, 6, 15000),
(17, 7, 40000),
(18, 8, 35000),
(19, 9, 22000),
(20, 10, 18000),
(11, 2, 10000),
(12, 3, 15000),
(13, 4, 25000),
(14, 5, 30000),
(15, 6, 17500),
(16, 7, 45000),
(17, 8, 12500),
(18, 9, 27500),
(19, 10, 19000),
(20, 1, 21000);

INSERT INTO issue (description) VALUES
('Healthcare'),
('Education'),
('Environment'),
('Tax Reform'),
('Criminal Justice'),
('Immigration'),
('Technology'),
('Infrastructure'),
('Civil Rights'),
('Foreign Policy');

INSERT INTO vote_cast (person_id, election_id, type, timestamp, voted_for) VALUES
(11, 1, 'in-person', '2025-06-01 09:00:00', 1),
(12, 1, 'mail', '2025-05-25 10:00:00', 2),
(13, 1, 'early', '2025-05-30 11:00:00', 3),
(14, 1, 'in-person', '2025-06-01 12:00:00', 4),
(15, 1, 'in-person', '2025-06-01 13:00:00', 5),
(16, 2, 'mail', '2025-10-28 10:00:00', 6),
(17, 2, 'early', '2025-10-30 11:00:00', 7),
(18, 2, 'in-person', '2025-11-01 12:00:00', 8),
(19, 2, 'in-person', '2025-11-01 13:00:00', 9),
(20, 2, 'mail', '2025-10-29 09:00:00', 10),
(11, 2, 'mail', '2025-10-27 10:00:00', 6),
(12, 2, 'early', '2025-10-30 11:30:00', 7),
(13, 2, 'in-person', '2025-11-01 12:15:00', 8),
(14, 2, 'in-person', '2025-11-01 13:45:00', 9),
(15, 2, 'mail', '2025-10-28 14:00:00', 10),
(16, 1, 'in-person', '2025-06-01 09:30:00', 2),
(17, 1, 'early', '2025-05-29 11:15:00', 1),
(18, 1, 'mail', '2025-05-27 10:45:00', 4),
(19, 1, 'in-person', '2025-06-01 14:00:00', 5),
(20, 1, 'mail', '2025-05-28 15:00:00', 3);

INSERT INTO attend_event (event_id, person_id, register_utm, register_timestamp) VALUES
(1, 11, 'utm_source=facebook&utm_medium=social&utm_campaign=voter_registration_push', '2025-04-15 10:00:00'),
(2, 12, 'utm_source=twitter&utm_medium=social&utm_campaign=debate_night_promo', '2025-04-15 10:05:00'),
(3, 13, 'utm_source=newsletter&utm_medium=email&utm_campaign=monthly_update_april', '2025-04-15 10:10:00'),
(4, 14, 'utm_source=google&utm_medium=cpc&utm_campaign=donation_drive_q2', '2025-04-15 10:15:00'),
(5, 15, 'utm_source=instagram&utm_medium=social&utm_campaign=behind_the_scenes', '2025-04-15 10:20:00'),
(6, 16, 'utm_source=volunteer_portal&utm_medium=referral&utm_campaign=volunteer_signup_may', '2025-04-15 10:25:00'),
(7, 17, 'utm_source=reddit&utm_medium=community&utm_campaign=ask_me_anything_event', '2025-04-15 10:30:00'),
(8, 18, 'utm_source=eventbrite&utm_medium=event&utm_campaign=town_hall_registration', '2025-04-15 10:35:00'),
(9, 19, 'utm_source=linkedIn&utm_medium=social&utm_campaign=policy_announcement', '2025-04-15 10:40:00'),
(10, 20, 'utm_source=website&utm_medium=organic&utm_campaign=issues_page_views', '2025-04-15 10:45:00'),
(1, 12, 'utm_source=mailchimp&utm_medium=email&utm_campaign=early_voting_reminder', '2025-04-16 11:00:00'),
(2, 13, 'utm_source=doorhanger&utm_medium=print&utm_campaign=local_outreach_effort', '2025-04-16 11:05:00'),
(3, 14, 'utm_source=tv_ad&utm_medium=broadcast&utm_campaign=debate_replay', '2025-04-16 11:10:00'),
(4, 15, 'utm_source=yard_sign&utm_medium=offline&utm_campaign=sign_request_form', '2025-04-16 11:15:00'),
(5, 16, 'utm_source=direct_mail&utm_medium=print&utm_campaign=absentee_ballot_info', '2025-04-16 11:20:00'),
(6, 17, 'utm_source=tiktok&utm_medium=social&utm_campaign=meet_the_candidate', '2025-04-16 11:25:00'),
(7, 18, 'utm_source=petition_site&utm_medium=referral&utm_campaign=endorsement_boost', '2025-04-16 11:30:00'),
(8, 19, 'utm_source=snapchat&utm_medium=social&utm_campaign=young_voters_outreach', '2025-04-16 11:35:00'),
(9, 20, 'utm_source=phonebank&utm_medium=referral&utm_campaign=donation_followup', '2025-04-16 11:40:00'),
(10, 11, 'utm_source=podcast&utm_medium=audio&utm_campaign=candidate_interview', '2025-04-16 11:45:00');

INSERT INTO person_issue (person_id, issue_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 1), (12, 2), (13, 3), (14, 4), (15, 5),
(16, 6), (17, 7), (18, 8), (19, 9), (20, 10);

-- USE CASES
-- (1)
DROP INDEX IF EXISTS person_by_district ON person;
CREATE INDEX person_by_distict ON person(district);

GO
CREATE OR ALTER PROCEDURE get_person
    @id AS INTEGER
AS BEGIN
    SELECT first, last, district FROM person WHERE person_id = @id;
END;

GO
CREATE OR ALTER PROCEDURE find_local_candidates
	@voter_id AS INTEGER
AS BEGIN
	DECLARE @district VARCHAR(5);

	SELECT @district = district FROM person WHERE person_id = @voter_id;

	SELECT first, last, district, election.election_id, date as election_date, campaign_id FROM campaign
	INNER JOIN person ON campaign.candidate_id = person_id
	INNER JOIN election ON campaign.election_id = election.election_id
	WHERE district = @district AND person_id != @voter_id
END;
GO

EXECUTE find_local_candidates
	@voter_id = 1;
GO

GO
CREATE OR ALTER PROCEDURE query_events
    @candidate_id AS INTEGER = NULL,
    @type_filter AS VARCHAR(20)
AS BEGIN
    SET NOCOUNT ON;

    SELECT * FROM event
    LEFT JOIN campaign ON event.campaign_id = campaign.campaign_id
    LEFT JOIN person ON campaign.candidate_id = person.person_id
    WHERE (
        @candidate_id IS NULL OR person.person_id = @candidate_id
    ) AND (
        @type_filter IS NULL
        OR EXISTS (
            SELECT value FROM STRING_SPLIT(@type_filter, ',')
            WHERE value = event.type
        )
    ) AND (
        time_end > SYSUTCDATETIME()
    )
    ORDER BY time_start ASC
END;
GO

EXECUTE query_events
    @type_filter = 'gotv';
GO

-- TODO: Why is venue_id null?

GO
CREATE OR ALTER PROCEDURE register_event
	@event_id AS INTEGER,
	@person_id AS INTEGER,
	@utm AS VARCHAR(255)
AS BEGIN
	SET NOCOUNT ON;
	SET IMPLICIT_TRANSACTIONS OFF;
	BEGIN TRANSACTION;

	DECLARE @attendees INTEGER, @capacity INTEGER;

	SELECT @attendees = COUNT(*) FROM attend_event WHERE event_id = @event_id;
	SELECT @capacity = max_capacity FROM event WHERE event_id = @event_id;

	IF @attendees + 1 > @capacity
		ROLLBACK TRANSACTION;
	ELSE BEGIN
		INSERT INTO attend_event(
			event_id,
			person_id,
			register_utm,
			register_timestamp
		) VALUES (
			@event_id,
			@person_id,
			@utm,
			SYSUTCDATETIME()
		);
		COMMIT TRANSACTION;
	END;
END
GO

EXECUTE register_event
	@event_id = 3,
	@person_id = 1,
	@utm = 'utm_source=cli';
GO

-- USE CASE (2)
CREATE OR ALTER PROCEDURE find_candidates_by_issue
	@issue_id AS INTEGER
AS BEGIN
	SELECT first, last, district, campaign_id, election.election_id, date as election_date
	FROM person
	INNER JOIN campaign ON campaign.candidate_id = person.person_id
	INNER JOIN election ON election.election_id = campaign.election_id
	INNER JOIN person_issue ON person_issue.person_id = person.person_id
	INNER JOIN issue ON person_issue.issue_id = issue.issue_id
	WHERE date > GETDATE()
END;
GO

EXECUTE find_candidate_by_issue
	@issue_id = 1;
GO

CREATE OR ALTER PROCEDURE make_donation
    @person_id INTEGER,
    @campaign_id INTEGER,
    @amount BIGINT,
	@statutory_limit BIGINT
AS
BEGIN
	BEGIN TRANSACTION;
	-- Do not allow donations to campaigns in the past.
	DECLARE @date DATE, @election_id INTEGER;

	SELECT @date = date, @election_id = campaign.election_id FROM election
	INNER JOIN campaign ON campaign.election_id = election.election_id
	WHERE campaign.campaign_id = @campaign_id;

	IF @date IS NULL OR @date < GETDATE() BEGIN
		ROLLBACK TRANSACTION;
		THROW 51000, 'Cannot donate to an election in the past', 1;
	END ELSE BEGIN
		DECLARE @cycle_sum BIGINT;

		SELECT @cycle_sum = SUM(amount) FROM donation
		INNER JOIN campaign ON campaign.campaign_id = donation.campaign_id
		WHERE person_id = @person_id AND election_id = @election_id;

		IF @cycle_sum + @amount > @statutory_limit BEGIN
			ROLLBACK TRANSACTION;
			THROW 51001, 'Donation would exceed statutory limit', 2;
		END ELSE BEGIN
			INSERT INTO donation VALUES (@person_id, @campaign_id, @amount);
			COMMIT TRANSACTION;
		END
	END
END;
GO

EXECUTE make_donation
	@person_id = 1,
	@campaign_id = 1,
	@amount = 10000,
	@statutory_limit = 20000;
GO

-- (6)
DROP PROCEDURE IF EXISTS find_new_candidates;
GO
CREATE PROCEDURE find_new_candidates
    @district VARCHAR(5),
    @issueId INT,
    @electionId INT
AS
BEGIN
    SET NOCOUNT ON;

    CREATE TABLE #CandidateScores (
        person_id INT,
        first NVARCHAR(100),
        last NVARCHAR(100),
        email NVARCHAR(100),
        phone NVARCHAR(20),
        elections_participated INT,
        matching_issue INT
    );

    INSERT INTO #CandidateScores (person_id, first, last, email, phone, elections_participated, matching_issue)
    SELECT 
        p.person_id,
        p.first,
        p.last,
        p.email,
        p.phone,
        (SELECT COUNT(*) FROM campaign c2 WHERE c2.candidate_id = p.person_id) AS elections_participated,
        (SELECT COUNT(*) FROM person_issue pi2 WHERE pi2.person_id = p.person_id AND pi2.issue_id = @issueId) AS matching_issue
    FROM person p
    LEFT JOIN campaign c ON c.candidate_id = p.person_id AND c.election_id = @electionId
    WHERE p.district = @district
      AND c.campaign_id IS NULL;


    SELECT TOP 5 * 
    FROM #CandidateScores
    WHERE matching_issue > 0
    ORDER BY elections_participated DESC, matching_issue DESC;

    DROP TABLE #CandidateScores;
END
GO


DROP PROCEDURE IF EXISTS add_candidate_to_campaign;
GO
CREATE PROCEDURE add_candidate_to_campaign
    @personId INT,
    @electionId INT,
    @managerId INT
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;

        IF EXISTS (
            SELECT 1 FROM campaign
            WHERE candidate_id = @personId AND election_id = @electionId
        )
        BEGIN
            RAISERROR('Candidate is already registered for this election.', 16, 1);
            ROLLBACK;
            RETURN;
        END;

        INSERT INTO campaign (candidate_id, manager_id, election_id, funds)
        VALUES (@personId, @managerId, @electionId, 0.00);

        COMMIT;
    END TRY
    BEGIN CATCH
        ROLLBACK;
        PRINT 'Error: ' + ERROR_MESSAGE();
    END CATCH
END
GO



-- (7)

DROP INDEX IF EXISTS person_by_name_and_dob ON person;
CREATE INDEX person_by_name_and_dob
ON person(first, last, dob);


GO
CREATE OR ALTER TRIGGER add_funds_after_donation
ON donation
AFTER INSERT
AS
BEGIN
    UPDATE campaign
    SET funds = campaign.funds + donated.total_amount
    FROM campaign INNER JOIN (
        SELECT campaign_id, SUM(amount) as total_amount FROM inserted GROUP BY campaign_id
    ) AS donated ON campaign.campaign_id = donated.campaign_id;
END;
GO

GO
CREATE OR ALTER TRIGGER remove_funds_after_donation_removal
ON donation
AFTER DELETE
AS
BEGIN
    UPDATE campaign
    SET funds = campaign.funds - donated.total_amount
    FROM campaign INNER JOIN (
        SELECT campaign_id, SUM(amount) as total_amount FROM deleted GROUP BY campaign_id
    ) AS donated ON campaign.campaign_id = donated.campaign_id;
END;
GO


GO
CREATE OR ALTER PROCEDURE insert_person
    @first VARCHAR(255),
    @last VARCHAR(255),
    @dob DATE,
    @phone VARCHAR(255) = NULL,
    @email VARCHAR(255) = NULL,
    @address VARCHAR(255) = NULL,
    @district VARCHAR(255) = NULL
AS
BEGIN
    INSERT INTO person OUTPUT Inserted.person_id VALUES(@first, @last, @dob, @phone, @email, @address, @district);
END;
GO

GO
CREATE OR ALTER PROCEDURE insert_donation
    @person_id INTEGER,
    @campaign_id INTEGER,
    @amount BIGINT
AS
BEGIN
    INSERT INTO donation VALUES (@person_id, @campaign_id, @amount);
END;
GO

GO
CREATE OR ALTER PROCEDURE delete_donation
    @donation_id INTEGER
AS
BEGIN
    DELETE FROM donation where donation.donation_id = @donation_id;
END;
GO


GO
CREATE OR ALTER PROCEDURE find_largest_donors
    @campaign_id INTEGER
AS
BEGIN
    SELECT person.first, person.last, person.phone, person.email, total_donations
    FROM person INNER JOIN (
        SELECT donation.person_id, SUM(donation.amount) AS total_donations 
        FROM donation 
        WHERE donation.campaign_id = @campaign_id
        GROUP BY donation.person_id
    ) AS donation_sum ON person.person_id = donation_sum.person_id
    ORDER BY donation_sum.total_donations;
END;
GO

GO
CREATE OR ALTER PROCEDURE check_campaign_similarity
    @campaign_a INTEGER,
    @campaign_b INTEGER
AS
BEGIN
    SELECT COUNT(*)
    FROM (
        SELECT person_issue.issue_id 
        FROM person_issue INNER JOIN campaign ON person_issue.person_id = campaign.candidate_id
        WHERE campaign.candidate_id = @campaign_a
        INTERSECT
        SELECT person_issue.issue_id
        FROM person_issue INNER JOIN campaign on person_issue.person_id = campaign.candidate_id
        WHERE campaign.candidate_id = @campaign_b
    ) as shared_issues;
END;
GO

-- (8)

GO
CREATE OR ALTER PROCEDURE insert_campaign
    @candidate_id INTEGER,
    @manager_id INTEGER,
    @election_id INTEGER
AS
BEGIN
    INSERT INTO campaign OUTPUT Inserted.campaign_id VALUES (@candidate_id, @manager_id, @election_id, 0);
END;
GO

GO
CREATE OR ALTER PROCEDURE insert_election
    @date DATE,
    @registration_deadline DATE
AS
BEGIN
    INSERT INTO election OUTPUT Inserted.election_id VALUES (@registration_deadline, @date);
END;
GO

GO
CREATE OR ALTER PROCEDURE find_people_for_issue
    @issue_id INTEGER
AS
BEGIN
    SELECT person.first, person.last, person.phone, person.email
    FROM person INNER JOIN person_issue ON person.person_id = person_issue.person_id
    WHERE person_issue.issue_id = @issue_id;
END;
GO

GO
CREATE OR ALTER PROCEDURE find_elections_for_issue
    @issue_id INTEGER
AS
BEGIN
    SELECT DISTINCT campaign.election_id
    FROM campaign INNER JOIN person_issue ON campaign.candidate_id = person_issue.person_id
    WHERE person_issue.issue_id = issue_id;
END;
GO

GO
CREATE OR ALTER PROCEDURE insert_person_issue
    @person_id INTEGER,
    @issue_id INTEGER
AS
BEGIN
    INSERT INTO person_issue OUTPUT Inserted.person_id, Inserted.issue_id VALUES (@person_id, @issue_id);
END;
GO

GO
CREATE OR ALTER PROCEDURE voted_in
    @election_id INTEGER
AS
BEGIN
    SELECT person.first, person.last, person.phone, person.email
    FROM person INNER JOIN vote_cast ON person.person_id = vote_cast.person_id
    WHERE vote_cast.election_id = @election_id
END;
GO

-- (12)
DROP PROCEDURE IF EXISTS analyze_event_effectiveness;
GO
CREATE OR ALTER PROCEDURE analyze_event_effectiveness
    @eventType VARCHAR(50) = NULL,
    @campaignId INT = NULL
AS BEGIN
	CREATE TABLE #EventSummary(
    event_id INT,
    total_attendance INT,
    top_issue_id INT,
    top_issue_count INT,
	)

    SELECT TOP 5 
        e.event_id,
        e.name AS event_name,
        COUNT(ae.person_id) AS total_attendance
    INTO #TopEvents
    FROM event e
    JOIN attend_event ae ON e.event_id = ae.event_id
    WHERE (@eventType IS NULL OR e.type = @eventType)
      AND (@campaignId IS NULL OR e.campaign_id = @campaignId)
    GROUP BY e.event_id, e.name
    ORDER BY total_attendance DESC;

    INSERT INTO #EventSummary(event_id, total_attendance, top_issue_id, top_issue_count)
    SELECT te.event_id,
        te.total_attendance,
        pi.issue_id,
        COUNT(*) AS top_issue_count
    FROM #TopEvents te
    JOIN attend_event ae ON ae.event_id = te.event_id
    JOIN person_issue pi ON pi.person_id = ae.person_id
    GROUP BY te.event_id, te.total_attendance, pi.issue_id
    HAVING COUNT(*) = (
        SELECT MAX(issue_count) FROM (
            SELECT COUNT(*) AS issue_count
            FROM attend_event ae2
            JOIN person_issue pi2 ON pi2.person_id = ae2.person_id
            WHERE ae2.event_id = te.event_id
            GROUP BY pi2.issue_id
        ) AS counts
    );

    DROP TABLE #TopEvents;

	SELECT es.event_id, e.name, es.total_attendance, i.description AS top_issue
	FROM #EventSummary es
	JOIN event e ON es.event_id = e.event_id
	JOIN issue i ON es.top_issue_id = i.issue_id;

	DROP TABLE #EventSummary;
END
GO
