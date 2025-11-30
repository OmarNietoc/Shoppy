import base64
import os

# Ruta de las imágenes
img_dir = r"c:/Users/acer/Desktop/Proyectos DUOC/HuertoHogarReact/public/img/products"

# Productos con sus imágenes
productos = [
    ("Manzanas Fuji", "Crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.", 1200, "frutas", "kg", "apples2.jpg", 100),
    ("Naranjas Valencia", "Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes. Cultivadas en condiciones climáticas óptimas.", 1000, "frutas", "kg", "oranges2.jpg", 80),
    ("Plátanos Cavendish", "Maduros y dulces, perfectos para el desayuno o como snack energético. Ricos en potasio y vitaminas.", 800, "frutas", "kg", "bananas.jpg", 120),
    ("Zanahorias Orgánicas", "Cultivadas sin pesticidas en la Región de O'Higgins. Excelente fuente de vitamina A y fibra, ideales para ensaladas y jugos.", 900, "verduras", "kg", "carrots.jpg", 90),
    ("Espinacas Frescas", "Frescas y nutritivas, perfectas para ensaladas y batidos verdes. Cultivadas bajo prácticas orgánicas que garantizan su calidad.", 700, "verduras", "kg", "spinach.jpg", 60),
    ("Pimientos Tricolores", "Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos. Ricos en antioxidantes y vitaminas.", 1500, "verduras", "kg", "peppers.jpg", 50),
    ("Miel Orgánica", "Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable.", 5000, "organicos", "500g", "honey.jpg", 30),
    ("Quinua Orgánica", "Quinua orgánica de alta calidad, rica en proteínas y nutrientes esenciales. Perfecta para una alimentación saludable.", 3500, "organicos", "kg", "quinoa.jpg", 40),
    ("Leche Entera", "Leche entera fresca de vacas criadas en praderas naturales. Rica en calcio y vitaminas esenciales.", 1800, "lacteos", "L", "milk.jpg", 70),
]

# Leer imágenes y convertir a Base64
def get_base64(filename):
    filepath = os.path.join(img_dir, filename)
    with open(filepath, "rb") as f:
        return base64.b64encode(f.read()).decode('utf-8')

# Generar SQL
sql_output = """-- ============================================
-- POBLAR CATEGORÍAS
-- ============================================
INSERT INTO categoria (nombre, descripcion) VALUES 
('frutas', 'Nuestra selección de frutas frescas ofrece una experiencia directa del campo a tu hogar. Estas frutas se cultivan y cosechan en el punto óptimo de madurez para asegurar su sabor y frescura. Disfruta de una variedad de frutas de temporada que aportan vitaminas y nutrientes esenciales a tu dieta diaria. Perfectas para consumir solas, en ensaladas o como ingrediente principal en postres y smoothies.');

INSERT INTO categoria (nombre, descripcion) VALUES 
('verduras', 'Descubre nuestra gama de verduras orgánicas, cultivadas sin el uso de pesticidas ni químicos, garantizando un sabor auténtico y natural. Cada verdura es seleccionada por su calidad y valor nutricional, ofreciendo una excelente fuente de vitaminas, minerales y fibra. Ideales para ensaladas, guisos y platos saludables, nuestras verduras orgánicas promueven una alimentación consciente y sostenible.');

INSERT INTO categoria (nombre, descripcion) VALUES 
('organicos', 'Nuestros productos orgánicos están elaborados con ingredientes naturales y procesados de manera responsable para mantener sus beneficios saludables. Desde aceites y miel hasta granos y semillas, ofrecemos una selección que apoya un estilo de vida saludable y respetuoso con el medio ambiente. Estos productos son perfectos para quienes buscan opciones alimenticias que aporten bienestar sin comprometer el sabor ni la calidad.');

INSERT INTO categoria (nombre, descripcion) VALUES 
('lacteos', 'Los productos lácteos de HuertoHogar provienen de granjas locales que se dedican a la producción responsable y de calidad. Ofrecemos una gama de leches, yogures y otros derivados que conservan su frescura y sabor auténtico. Ricos en calcio y nutrientes esenciales, nuestros lácteos son perfectos para complementar una dieta equilibrada, proporcionando el mejor sabor y nutrición para toda la familia.');

-- ============================================
-- POBLAR UNIDADES (medidas.json)
-- ============================================
INSERT INTO unidad (nombre) VALUES ('kg');
INSERT INTO unidad (nombre) VALUES ('500g');
INSERT INTO unidad (nombre) VALUES ('L');
INSERT INTO unidad (nombre) VALUES ('unidad');

-- ============================================
-- POBLAR PRODUCTOS CON IMÁGENES BASE64
-- ============================================

"""

for nombre, desc, precio, cat, unid, img_file, stock in productos:
    # Escapar comillas simples en descripción
    desc_escaped = desc.replace("'", "''")
    
    # Obtener Base64 de la imagen
    img_base64 = get_base64(img_file)
    
    sql_output += f"""-- {nombre}
INSERT INTO producto (nombre, descripcion, precio, id_categoria, id_unidad, stock, imagen_bytes) 
SELECT '{nombre}', 
       '{desc_escaped}', 
       {precio}, 
       c.id, 
       u.id, 
       {stock},
       decode('{img_base64}', 'base64')
FROM categoria c, unidad u 
WHERE c.nombre = '{cat}' AND u.nombre = '{unid}';

"""

# Guardar archivo
output_path = r"c:/Users/acer/Desktop/Proyectos DUOC/Shoppy/Catalog/src/main/resources/import.sql"
with open(output_path, "w", encoding="utf-8") as f:
    f.write(sql_output)

print(f"Archivo generado: {output_path}")
print(f"{len(productos)} productos con imágenes Base64")
