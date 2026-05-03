import request from "@/utils/request";

export function getPolls(params) {
  return request.get("/polls", { params });
}

export function getPoll(id) {
  return request.get(`/polls/${id}`);
}

export function createPoll(data) {
  return request.post("/polls", data);
}

export function updatePoll(id, data) {
  return request.put(`/polls/${id}`, data);
}

export function deletePoll(id) {
  return request.delete(`/polls/${id}`);
}

export function getPollStats(id) {
  return request.get(`/polls/${id}/stats`);
}

export function addCandidate(pollId, data) {
  return request.post(`/polls/${pollId}/candidates`, data);
}

export function updateCandidate(id, data) {
  return request.put(`/polls/candidates/${id}`, data);
}

export function deleteCandidate(id) {
  return request.delete(`/polls/candidates/${id}`);
}
