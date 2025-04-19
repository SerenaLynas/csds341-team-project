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

CREATE TABLE donation (
    donation_id INTEGER IDENTITY PRIMARY KEY,
    person_id INTEGER NOT NULL,
    campaign_id INTEGER NOT NULL,
    amount NUMERIC NOT NULL,
    FOREIGN KEY (person_id) REFERENCES person(person_id),
    FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)
);

CREATE TABLE issue (
    issue_id INTEGER IDENTITY PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

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

CREATE TABLE attend_event (
    event_id INTEGER NOT NULL,
    person_id INTEGER NOT NULL,
    register_utm VARCHAR(255),
    register_timestamp DATETIME2 DEFAULT (SYSUTCDATETIME()) NOT NULL,
    PRIMARY KEY (event_id, person_id),
    FOREIGN KEY (event_id) REFERENCES event(event_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);

CREATE TABLE person_issue (
    person_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    PRIMARY KEY (person_id, issue_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id),
    FOREIGN KEY (issue_id) REFERENCES issue(issue_id)
);
