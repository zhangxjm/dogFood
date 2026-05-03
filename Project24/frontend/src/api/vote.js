import request from "@/utils/request";

export function castVote(data) {
  return request.post("/votes", data);
}

export function checkVoteStatus(pollId) {
  return request.get("/votes/check", { params: { poll_id: pollId } });
}
