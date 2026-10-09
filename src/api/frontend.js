import service from '@/utils/request'

export function startSession(data) {
  return service.post('/psychological-chat/session/start', data)
}

export function getSessionList(params) {
  return service.get('/psychological-chat/sessions', { params })
}

export function getSessionDetail(sessionId) {
  return service.get('/psychological-chat/session/' + sessionId)
}

export function deleteSession(sessionId) {
  return service.delete('/psychological-chat/session/' + sessionId)
}

export function getSessionEmotion(sessionId) {
  return service.get('/psychological-chat/session/' + sessionId + '/emotion')
}

export function getKnowledgeList(params) {
  return service.get('/knowledge/article/page', { params })
}

export function getKnowledgeDetail(id) {
  return service.get('/knowledge/article/' + id)
}
