-- BorrowBox Standalone Seed Data Script

USE borrowbox_db;

-- 1. SEED USERS (Password for all demo accounts: Password123!)
INSERT INTO users (id, full_name, email, phone, password_hash, role, account_status, avatar_url) VALUES
(1, 'Admin User', 'admin@borrowbox.com', '+91 98765 00001', '$2a$10$qlHn1a6K185nPLKq7tBX0OfL.ry7vPfE2Q0bPTfLK4HsqxBhohK3W', 'ROLE_ADMIN', 'ACTIVE', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80'),
(2, 'Rahul Sharma (Owner)', 'rahul.owner@borrowbox.com', '+91 98765 11111', '$2a$10$qlHn1a6K185nPLKq7tBX0OfL.ry7vPfE2Q0bPTfLK4HsqxBhohK3W', 'ROLE_OWNER', 'ACTIVE', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80'),
(3, 'Priya Patel (Owner)', 'priya.owner@borrowbox.com', '+91 98765 22222', '$2a$10$qlHn1a6K185nPLKq7tBX0OfL.ry7vPfE2Q0bPTfLK4HsqxBhohK3W', 'ROLE_OWNER', 'ACTIVE', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80'),
(4, 'Alex Rivera (Renter)', 'alex.renter@borrowbox.com', '+91 98765 33333', '$2a$10$qlHn1a6K185nPLKq7tBX0OfL.ry7vPfE2Q0bPTfLK4HsqxBhohK3W', 'ROLE_RENTER', 'ACTIVE', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80'),
(5, 'Sneha Kapoor (Renter)', 'sneha.renter@borrowbox.com', '+91 98765 44444', '$2a$10$qlHn1a6K185nPLKq7tBX0OfL.ry7vPfE2Q0bPTfLK4HsqxBhohK3W', 'ROLE_RENTER', 'ACTIVE', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80');

-- 2. SEED CATEGORIES
INSERT INTO categories (id, name, slug, description, icon_name, active) VALUES
(1, 'Cameras & Lenses', 'cameras-lenses', 'DSLRs, mirrorless cameras, lenses, tripods, and cinema gear', 'camera', TRUE),
(2, 'Laptops & Computers', 'laptops-computers', 'High-performance laptops, workstations, and tablets', 'laptop', TRUE),
(3, 'Projectors & Screens', 'projectors-screens', '4K/HD projectors, presentation screens, and accessories', 'projector', TRUE),
(4, 'Audio & Microphones', 'audio-microphones', 'Wireless mics, studio monitors, speakers, and podcast recorders', 'mic', TRUE),
(5, 'Lighting & Studio', 'lighting-studio', 'LED panel lights, softboxes, ring lights, and backdrops', 'sun', TRUE),
(6, 'Gaming & VR', 'gaming-vr', 'PlayStation 5, Xbox Series X, Nintendo Switch, and VR headsets', 'gamepad-2', TRUE),
(7, 'Power Tools & DIY', 'power-tools-diy', 'Drills, saws, pressure washers, and home improvement toolkits', 'wrench', TRUE),
(8, 'Electronics & Gadgets', 'electronics-gadgets', 'Drones, gimbals, action cams, and power stations', 'cpu', TRUE),
(9, 'Event & Party Equipment', 'event-party', 'PA sound systems, DJ decks, smoke machines, and staging', 'music', TRUE);

-- 3. SEED EQUIPMENT (15 Realistic Listings)
INSERT INTO equipment (id, owner_id, category_id, name, slug, description, brand, model, item_condition, daily_price, security_deposit, minimum_rental_days, maximum_rental_days, location, rental_instructions, rental_terms, status, rating_average, review_count) VALUES
(1, 2, 1, 'Sony Alpha A7 IV Mirrorless Camera', 'sony-alpha-a7-iv-mirrorless-camera', 'Full-frame 33MP hybrid camera ideal for high-end photography and 4K 60p video shooting. Comes with 2 batteries, 128GB SD card, and neck strap.', 'Sony', 'A7 IV', 'LIKE_NEW', 2500.00, 5000.00, 1, 14, 'Indiranagar, Bengaluru', 'Handle with care. Always keep in protective padded bag during transit.', 'Government ID required at pickup. Any sensor damage will incur repair cost from deposit.', 'AVAILABLE', 4.9, 8),
(2, 2, 1, 'Canon EOS R6 Mark II + 24-70mm f/2.8L', 'canon-eos-r6-mark-ii-24-70mm', 'Professional full-frame camera kit with ultra-fast autofocus and 4K 60p uncropped video. Includes 24-70mm f/2.8L lens and dual 128GB V60 cards.', 'Canon', 'EOS R6 Mark II', 'EXCELLENT', 3200.00, 6000.00, 1, 10, 'Koramangala, Bengaluru', 'Verify lens glass clarity upon pickup.', 'Deposit fully refundable upon damage-free return.', 'AVAILABLE', 5.0, 5),
(3, 3, 1, 'DJI Ronin RS 3 Pro Gimbal Stabilizer', 'dji-ronin-rs-3-pro-gimbal', 'Automated axis locks, extended carbon fiber axis arms, and 4.5kg payload capacity. Perfect for heavy DSLR & cinema setups.', 'DJI', 'RS 3 Pro', 'EXCELLENT', 1400.00, 3000.00, 1, 7, 'Bandra West, Mumbai', 'Make sure to balance setup correctly before powering on motors.', 'Over-torquing motors will forfeit deposit.', 'AVAILABLE', 4.8, 4),
(4, 2, 2, 'Apple MacBook Pro 16" M3 Max (36GB RAM)', 'apple-macbook-pro-16-m3-max', 'Powerhouse laptop for 8K video editing, 3D rendering, and software development. 1TB NVMe storage.', 'Apple', 'MacBook Pro 16 M3 Max', 'LIKE_NEW', 3500.00, 10000.00, 2, 30, 'Indiranagar, Bengaluru', 'MacBook is logged out of iCloud. Keep charger connected during heavy export.', 'System must be returned logged out of all personal accounts.', 'AVAILABLE', 4.95, 6),
(5, 3, 2, 'Dell XPS 15 9530 i9 RTX 4070', 'dell-xps-15-9530-i9-rtx-4070', '3.5K OLED Touch screen display, Intel i9 13th Gen, 32GB RAM, 1TB SSD. Great for creators and mobile workstation needs.', 'Dell', 'XPS 15 9530', 'GOOD', 2200.00, 5000.00, 1, 15, 'Andheri West, Mumbai', 'Includes original 130W USB-C fast charger and neoprene sleeve.', 'Strict no liquid near keyboard policy.', 'AVAILABLE', 4.7, 3),
(6, 3, 3, 'Epson EF-12 Mini Laser Smart Projector', 'epson-ef-12-mini-laser-smart-projector', 'Compact Full HD 1080p Smart Laser Projector with built-in Sound by Yamaha and Android TV. Up to 150-inch display.', 'Epson', 'EF-12', 'EXCELLENT', 1200.00, 2500.00, 1, 7, 'Powai, Mumbai', 'Keep ventilation grills clear during operation.', 'HDMI cable and remote control included.', 'AVAILABLE', 4.85, 7),
(7, 2, 3, 'Optoma UHD38x 4K UHD Home Theater Projector', 'optoma-uhd38x-4k-uhd-projector', 'True 4K UHD 4000 Lumens HDR gaming and movie projector with 4.2ms low input lag.', 'Optoma', 'UHD38x', 'EXCELLENT', 1800.00, 4000.00, 1, 10, 'HSR Layout, Bengaluru', 'Mount or place on flat stable surface.', 'Avoid touching optical lens glass.', 'AVAILABLE', 4.6, 2),
(8, 2, 4, 'Sennheiser EW 112P G4 Wireless Mic System', 'sennheiser-ew-112p-g4-wireless-mic', 'Industry-standard broadcast wireless lavalier microphone system for crisp documentary and interview audio.', 'Sennheiser', 'EW 112P G4', 'EXCELLENT', 950.00, 2000.00, 1, 14, 'Koramangala, Bengaluru', 'AA batteries included. Check channel frequency before recording.', 'Handle lavalier wire gently.', 'AVAILABLE', 4.9, 9),
(9, 3, 4, 'Rode Caster Pro II Audio Production Studio', 'rode-caster-pro-ii-audio-production-studio', 'Ultimate audio solution for podcasters, streamers, and musicians. 4 combo inputs and customizable SMART pads.', 'Rode', 'RodeCaster Pro II', 'LIKE_NEW', 1600.00, 3500.00, 1, 10, 'Juhu, Mumbai', 'Pre-configured SD card included. USB-C cables provided.', 'Do not modify internal firmware settings.', 'AVAILABLE', 5.0, 4),
(10, 2, 5, 'Godox SL-60W LED Video Light + Softbox Kit', 'godox-sl-60w-led-video-light-softbox-kit', '60W daylight-balanced continuous LED light kit with Bowens mount 80cm softbox and light stand.', 'Godox', 'SL-60W Kit', 'EXCELLENT', 750.00, 1500.00, 1, 7, 'Indiranagar, Bengaluru', 'Allow bulb housing to cool down before packing into transport bag.', 'Stands must be tightened securely.', 'AVAILABLE', 4.75, 11),
(11, 3, 6, 'Sony PlayStation 5 Console + 2 Controllers', 'sony-playstation-5-console-2-controllers', 'PS5 Disc Edition with DualSense wireless controllers and pre-installed popular multiplayer games.', 'Sony', 'PlayStation 5', 'LIKE_NEW', 1100.00, 3000.00, 1, 14, 'Bandra West, Mumbai', 'Console reset to guest mode. Connect HDMI to high-speed port.', 'Controllers must be returned damage-free.', 'AVAILABLE', 4.95, 15),
(12, 2, 7, 'Bosch GSB 18V-55 Cordless Hammer Drill Kit', 'bosch-gsb-18v-55-cordless-hammer-drill-kit', 'Heavy-duty 18V brushless drill driver with 2x 4.0Ah batteries, charger, and 35-piece drill bit set.', 'Bosch', 'GSB 18V-55', 'GOOD', 650.00, 1200.00, 1, 7, 'Whitefield, Bengaluru', 'Wear safety goggles while drilling masonry or steel.', 'Clean dust off drill bits before packing.', 'AVAILABLE', 4.8, 6),
(13, 3, 8, 'DJI Mini 4 Pro Fly More Combo Drone', 'dji-mini-4-pro-fly-more-combo-drone', 'Under 249g ultralight drone with 4K 60p HDR video, omnidirectional obstacle sensing, and RC 2 controller screen.', 'DJI', 'Mini 4 Pro', 'LIKE_NEW', 2800.00, 7000.00, 1, 7, 'South Mumbai, Mumbai', 'Check local drone flying regulations and restrictions before takeoff.', 'Renter is responsible for propeller or body crash damage.', 'AVAILABLE', 5.0, 8),
(14, 2, 8, 'GoPro HERO12 Black Action Camera Bundle', 'gopro-hero12-black-action-camera-bundle', '5.3K video, HyperSmooth 6.0 stabilization, waterproof to 33ft. Includes chest mount, suction cup, and 3 batteries.', 'GoPro', 'HERO12 Black', 'EXCELLENT', 850.00, 2000.00, 1, 10, 'Indiranagar, Bengaluru', 'Ensure battery door seal is locked before underwater use.', 'Wash off saltwater with fresh water if used in sea.', 'AVAILABLE', 4.85, 12),
(15, 3, 9, 'JBL PartyBox 310 Portable Bluetooth PA Speaker', 'jbl-partybox-310-portable-bluetooth-pa-speaker', '240W powerful JBL Pro sound with dynamic light show, built-in wheels, and 18-hour battery life.', 'JBL', 'PartyBox 310', 'EXCELLENT', 1500.00, 3000.00, 1, 5, 'Worli, Mumbai', 'Keep volume below max threshold to prevent driver distortion.', 'Do not submerge speaker in pool water.', 'AVAILABLE', 4.9, 10);

-- 4. SEED EQUIPMENT IMAGES
INSERT INTO equipment_images (equipment_id, file_path, alt_text, display_order, is_primary) VALUES
(1, 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80', 'Sony Alpha A7 IV Front View', 1, TRUE),
(1, 'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&w=800&q=80', 'Sony Alpha A7 IV Lens Mount', 2, FALSE),
(2, 'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?auto=format&fit=crop&w=800&q=80', 'Canon EOS R6 Mark II', 1, TRUE),
(3, 'https://images.unsplash.com/photo-1589872596407-c1c5905d6cb9?auto=format&fit=crop&w=800&q=80', 'DJI RS 3 Pro Gimbal', 1, TRUE),
(4, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80', 'Apple MacBook Pro 16', 1, TRUE),
(5, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?auto=format&fit=crop&w=800&q=80', 'Dell XPS 15 Laptop', 1, TRUE),
(6, 'https://images.unsplash.com/photo-1535016120720-40c646be5580?auto=format&fit=crop&w=800&q=80', 'Smart Home Projector', 1, TRUE),
(7, 'https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?auto=format&fit=crop&w=800&q=80', '4K Home Cinema Projector', 1, TRUE),
(8, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80', 'Wireless Mic Receiver and Transmitter', 1, TRUE),
(9, 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=800&q=80', 'Rode Caster Podcast Mixer', 1, TRUE),
(10, 'https://images.unsplash.com/photo-1527011046414-4781f1f94f8c?auto=format&fit=crop&w=800&q=80', 'LED Studio Softbox Light', 1, TRUE),
(11, 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=800&q=80', 'PlayStation 5 Console', 1, TRUE),
(12, 'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=800&q=80', 'Cordless Hammer Power Drill', 1, TRUE),
(13, 'https://images.unsplash.com/photo-1527977966376-1c8408f9f108?auto=format&fit=crop&w=800&q=80', 'DJI Mini 4 Pro Drone in flight', 1, TRUE),
(14, 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?auto=format&fit=crop&w=800&q=80', 'GoPro HERO Action Camera', 1, TRUE),
(15, 'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=800&q=80', 'JBL PartyBox Bluetooth Speaker', 1, TRUE);

-- 5. SEED BOOKINGS
INSERT INTO bookings (id, booking_reference, equipment_id, renter_id, owner_id, start_date, end_date, rental_days, daily_price_snapshot, rental_subtotal, additional_fees, deposit_amount, total_amount, booking_status, payment_status, payment_transaction_id) VALUES
(1, 'BB-2026-8801', 1, 4, 2, '2026-10-15', '2026-10-17', 3, 2500.00, 7500.00, 150.00, 5000.00, 12650.00, 'APPROVED', 'PAID', 'TXN-991204'),
(2, 'BB-2026-8802', 11, 5, 3, '2026-10-12', '2026-10-14', 3, 1100.00, 3300.00, 100.00, 3000.00, 6400.00, 'ACTIVE', 'PAID', 'TXN-991205'),
(3, 'BB-2026-8803', 4, 5, 2, '2026-09-20', '2026-09-23', 4, 3500.00, 14000.00, 250.00, 10000.00, 24250.00, 'COMPLETED', 'PAID', 'TXN-991100'),
(4, 'BB-2026-8804', 13, 4, 3, '2026-10-25', '2026-10-27', 3, 2800.00, 8400.00, 200.00, 7000.00, 15600.00, 'PENDING', 'PAID', 'TXN-991299');

-- 6. SEED BOOKING STATUS HISTORY
INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_id, notes) VALUES
(1, NULL, 'PENDING', 4, 'Booking request submitted by renter.'),
(1, 'PENDING', 'APPROVED', 2, 'Owner approved booking request.'),
(2, NULL, 'PENDING', 5, 'Booking request submitted by renter.'),
(2, 'PENDING', 'APPROVED', 3, 'Owner approved booking.'),
(2, 'APPROVED', 'READY_FOR_PICKUP', 3, 'Equipment prepped for pickup.'),
(2, 'READY_FOR_PICKUP', 'ACTIVE', 3, 'Renter collected equipment after condition inspection.'),
(3, 'ACTIVE', 'RETURN_REQUESTED', 5, 'Renter returned equipment to owner.'),
(3, 'RETURN_REQUESTED', 'COMPLETED', 2, 'Owner verified item condition and completed rental.');

-- 7. SEED CONDITION REPORTS
INSERT INTO condition_reports (id, booking_id, reporter_id, inspection_stage, condition_notes, damage_notes, confirmed_at) VALUES
(1, 2, 3, 'BEFORE_HANDOVER', 'PS5 console and both controllers in pristine working condition. Zero scratches on glossy middle strip.', 'None. Handed over with power cable, HDMI 2.1 cable, and 2 DualSense controllers.', CURRENT_TIMESTAMP),
(2, 3, 2, 'BEFORE_HANDOVER', 'MacBook Pro in like-new condition. Clean keyboard and screen.', 'No scratches.', CURRENT_TIMESTAMP),
(3, 3, 2, 'AFTER_RETURN', 'Returned in exact pristine condition with all accessories.', 'No damage detected.', CURRENT_TIMESTAMP);

-- 8. SEED WISHLISTS
INSERT INTO wishlists (user_id, equipment_id) VALUES
(4, 1),
(4, 4),
(4, 13),
(5, 2),
(5, 11);

-- 9. SEED REVIEWS
INSERT INTO reviews (booking_id, equipment_id, renter_id, rating, comment) VALUES
(3, 4, 5, 5, 'Absolute beast of a machine! Processed 4K 10-bit video edits smoothly without dropping a single frame. Rahul was very responsive and helpful during handover.');

-- 10. SEED NOTIFICATIONS
INSERT INTO notifications (user_id, title, message, type, related_booking_id, is_read) VALUES
(2, 'New Booking Request', 'Alex Rivera has requested to book Sony Alpha A7 IV for Oct 15 - Oct 17.', 'BOOKING_REQUEST', 1, TRUE),
(4, 'Booking Approved!', 'Rahul Sharma approved your booking request for Sony Alpha A7 IV.', 'BOOKING_APPROVED', 1, FALSE),
(3, 'New Booking Request', 'Alex Rivera has requested to book DJI Mini 4 Pro for Oct 25 - Oct 27.', 'BOOKING_REQUEST', 4, FALSE),
(5, 'Rental Active', 'Your rental for PlayStation 5 Console is now active. Enjoy!', 'RENTAL_ACTIVE', 2, TRUE);
