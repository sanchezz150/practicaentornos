cena=float(input("ingrese el valor de la cena"))
propina=float(input("ingrese el porcentaje de la propina"))
total= cena+propina
def calcular_propina(cena,propina):
    cena= total*propina/100
    return cena
def calcular_total(cena,propina):
    total=cena+propina
    return total
print("el valor de la cena es",cena)
print("el valor de la propina es",propina)
print("el valor de la cena total es",total)
