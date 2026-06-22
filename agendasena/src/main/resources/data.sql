-- ===================== AMBIENTES =====================
INSERT INTO ambientes (nombre, tipo, capacidad, activo) VALUES
('Sala 101', 'SALA', 30, true),
('Laboratorio de Redes', 'LABORATORIO', 20, true),
('Auditorio Principal', 'AUDITORIO', 100, true),
('Sala 202', 'SALA', 25, true),
('Laboratorio de Sistemas', 'LABORATORIO', 15, false);

-- ===================== RESERVAS INICIALES =====================
-- Usamos fechas fijas en el futuro (ajusta el año si ya pasó al probar).
-- Sala 101 (id 1): reserva activa de 8:00 a 10:00
INSERT INTO reservas (ambiente_id, nombre_instructor, fecha_hora_inicio, fecha_hora_fin, numero_aprendices, estado) VALUES
(1, 'Carlos Ramirez', '2026-07-01 08:00:00', '2026-07-01 10:00:00', 25, 'ACTIVA');

-- Laboratorio de Redes (id 2): reserva activa de 14:00 a 16:00
INSERT INTO reservas (ambiente_id, nombre_instructor, fecha_hora_inicio, fecha_hora_fin, numero_aprendices, estado) VALUES
(2, 'Maria Gonzalez', '2026-07-01 14:00:00', '2026-07-01 16:00:00', 18, 'ACTIVA');

-- Auditorio Principal (id 3): reserva ya cancelada (para probar historial)
INSERT INTO reservas (ambiente_id, nombre_instructor, fecha_hora_inicio, fecha_hora_fin, numero_aprendices, estado) VALUES
(3, 'Carlos Ramirez', '2026-07-02 09:00:00', '2026-07-02 11:00:00', 80, 'CANCELADA');

-- Sala 202 (id 4): reserva activa de 9:00 a 11:00 el mismo día que la de Sala 101
INSERT INTO reservas (ambiente_id, nombre_instructor, fecha_hora_inicio, fecha_hora_fin, numero_aprendices, estado) VALUES
(4, 'Maria Gonzalez', '2026-07-01 09:00:00', '2026-07-01 11:00:00', 20, 'ACTIVA');