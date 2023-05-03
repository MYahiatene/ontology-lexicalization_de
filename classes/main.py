with open('classes.txt','r') as f:
    with open('output.txt','w') as f2:
        for line in f:
            clazz ='<'+line.split('\n')[0]+'> '
            #print(clazz)
            f2.write(clazz)