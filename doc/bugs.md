boton quitado:
<Button
size="lg"
variant={isInWishlist(product.id) ? 'default' : 'outline'}
onClick={handleToggleWishlist}
>
<Heart className={`h-5 w-5 ${isInWishlist(product.id) ? 'fill-current' : ''}`} />
</Button>