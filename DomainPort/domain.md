user 
 - user
 - coupon { user, price }

product
 - product
 - option

order
 - order { product, coupon, List<OrderItem>, date }
 - order item { order, product, option, count }

