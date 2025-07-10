BEGIN;  
INSERT INTO app.club (id, name, balance, admin, created) VALUES  
    (gen_random_uuid(), 'club Vibes', 100, 'Henry', now()),  
    (gen_random_uuid(), 'best Club', 1, 'Steve', now());
COMMIT;
