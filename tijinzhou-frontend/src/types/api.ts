export type BaseResponse<T> = {
  code: number;
  data: T;
  message: string;
};

export type PageData<T> = {
  records?: T[];
  total?: number | string;
  current?: number | string;
  size?: number | string;
};

export type LoginUser = {
  id: string;
  userAccount?: string;
  userName?: string;
  userAvatar?: string;
  userProfile?: string;
  userRole?: string;
  userStatus?: number;
  createTime?: string;
};

export type QuestionBank = {
  id: string;
  title?: string;
  description?: string;
  picture?: string;
  userId?: string;
  createTime?: string;
  updateTime?: string;
};

export type Question = {
  id: string;
  title?: string;
  content?: string;
  tagList?: string[];
  answer?: string;
  userId?: string;
  status?: number;
  createTime?: string;
  updateTime?: string;
  questionBankList?: QuestionBank[];
};

export type QuestionBankQuestion = {
  id: string;
  question_bank_id?: string;
  question_id?: string;
  questionBankId?: string;
  questionId?: string;
};

export type SignResult = {
  signed: boolean;
  alreadySigned: boolean;
  signDate: string;
  monthSignCount: number;
  streak: number;
};

export type SignCalendarDay = {
  date: string;
  signed: boolean;
  practiceCount: number;
};

export type SignCalendar = {
  year: number;
  month: number;
  today: string;
  signedToday: boolean;
  monthSignCount: number;
  streak: number;
  days: SignCalendarDay[];
};

export type SignYearDay = {
  date: string;
  signed: boolean;
};

export type SignYear = {
  year: number;
  today: string;
  days: SignYearDay[];
};

export type SignDayItem = {
  id: string;
  title: string;
};

export type SignDay = {
  date: string;
  signed: boolean;
  questions: SignDayItem[];
};

export function pageTotal(page?: { total?: number | string }) {
  return Number(page?.total ?? 0);
}

export function relationBankId(item: QuestionBankQuestion) {
  return String(item.question_bank_id ?? item.questionBankId ?? "");
}
