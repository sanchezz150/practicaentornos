precio=float(input("ingrese el precio del producto"))
tasa=float(input("ingrese los impuestos del producto"))
cargos=float(input("ingrese los cargos de envio"))
total= precio+tasa+cargos
def calcular_precio_total(precio,cargos,tasa):
    total=precio+cargos+tasa
    return total
def calcular_tasa_impuestos(precio,tasa):
    tasa=precio*tasa/100
    return tasa
print("los impuestos son",tasa)
print("la cantidad total",total)

