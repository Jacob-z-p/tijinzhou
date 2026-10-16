"use client";

import ReactMarkdown from "react-markdown";

export default function MarkdownView({ content }: { content?: string }) {
  if (!content) {
    return <p style={{ color: "#999" }}>暂无内容</p>;
  }
  return (
    <div className="markdown-body">
      <ReactMarkdown>{content}</ReactMarkdown>
    </div>
  );
}
