-- ============================================
-- POBLAR CATEGORÍAS (Idempotente)
-- ============================================
INSERT INTO categories (name) VALUES ('frutas') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('verduras') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('organicos') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('lacteos') ON CONFLICT (name) DO NOTHING;

-- ============================================
-- POBLAR UNIDADES (Idempotente)
-- ============================================
INSERT INTO units (name) VALUES ('kg') ON CONFLICT (name) DO NOTHING;
INSERT INTO units (name) VALUES ('500g') ON CONFLICT (name) DO NOTHING;
INSERT INTO units (name) VALUES ('L') ON CONFLICT (name) DO NOTHING;
INSERT INTO units (name) VALUES ('units') ON CONFLICT (name) DO NOTHING;

-- ============================================
-- LOS PRODUCTOS SE CARGAN AUTOMÁTICAMENTE
-- mediante DatabaseInitializer.java
-- ============================================
