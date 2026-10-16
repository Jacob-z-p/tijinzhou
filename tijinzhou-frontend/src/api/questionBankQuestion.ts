import { post } from "@/libs/request";
import { relationBankId, type PageData, type QuestionBankQuestion } from "@/types/api";

export function listQuestionBankQuestion(body: {
  current?: number;
  pageSize?: number;
  questionId?: string;
  questionBankId?: string;
}) {
  return post<PageData<QuestionBankQuestion>>("/api/question_bank_question/list/page", body);
}

export function addQuestionBankQuestion(body: { questionBankId: string; questionId: string }) {
  return post<string>("/api/question_bank_question/add", body);
}

export function deleteQuestionBankQuestion(id: string) {
  return post<boolean>("/api/question_bank_question/delete", { id });
}

export async function syncQuestionBanks(questionId: string, nextBankIds: string[]) {
  const res = await listQuestionBankQuestion({
    questionId,
    current: 1,
    pageSize: 200,
  });
  const current = res.data?.records ?? [];
  const nextSet = new Set(nextBankIds);
  const currentBankIds = new Set(current.map(relationBankId));
  for (const bankId of nextBankIds) {
    if (bankId && !currentBankIds.has(bankId)) {
      await addQuestionBankQuestion({ questionBankId: bankId, questionId });
    }
  }
  for (const item of current) {
    const bankId = relationBankId(item);
    if (bankId && !nextSet.has(bankId)) {
      await deleteQuestionBankQuestion(String(item.id));
    }
  }
}
