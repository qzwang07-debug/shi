import request from '@/utils/request'

// 查询游戏性能管理列表
export function listGameInfo(query) {
  return request({
    url: '/system/gameInfo/list',
    method: 'get',
    params: query
  })
}

// 查询游戏性能管理详细
export function getGameInfo(gameId) {
  return request({
    url: '/system/gameInfo/' + gameId,
    method: 'get'
  })
}

// 新增游戏性能管理
export function addGameInfo(data) {
  return request({
    url: '/system/gameInfo',
    method: 'post',
    data: data
  })
}

// 修改游戏性能管理
export function updateGameInfo(data) {
  return request({
    url: '/system/gameInfo',
    method: 'put',
    data: data
  })
}

// 删除游戏性能管理
export function delGameInfo(gameId) {
  return request({
    url: '/system/gameInfo/' + gameId,
    method: 'delete'
  })
}
