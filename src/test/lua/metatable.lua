mymetatable = {}
mytable = setmetatable({key1 = "value1"}, {
    __newindex = function(mytable)
        print(mytable)
    end
})

print(mytable.key1)

mytable.newkey = "新值2"
print(mytable.newkey,mymetatable.newkey)

mytable.key1 = "新值1"
print(mytable.key1,mymetatable.newkey1)