use full;
SET SQL_SAFE_UPDATES = 1;
SET SQL_SAFE_UPDATES = 0;
UPDATE products
SET thumbnail = (
SELECT image_url
FROM productimages
WHERE products.id = productimages.product_id
LIMIT 1
)