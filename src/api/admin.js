import service from '@/utils/request'

export function login(data){
    return service.post('/user/login',data)
}

export function logout(){
    return service.post('/user/logout')
}
