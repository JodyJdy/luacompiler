function iterGen(tab)
    return function(tab, iterVar)
        if iterVar == nil then
            return 1, tab[1]
        else
            if tab[iterVar + 1] == nil then
                return nil
            end
            return iterVar + 1, tab[iterVar + 1]
        end
    end, tab, nil
end
function foreach(iterator, tab, iterVar, func)
    local v1, v2 = iterator(tab, iterVar)
    if v1 == nil then
        return nil
    end
    func(v1, v2)
    return foreach(iterator, tab, v1, func)
end

a = {'a', 'b', 'c', 'd', 'e'}
i, t, v = iterGen(a)

function iterBody(v1, v2)
    print(v1, v2)
end

foreach(i, t, v, iterBody)