import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Ant Design 的样式哈希会用到 Math.random，和 Cache Components 的预渲染冲突
  cacheComponents: false,
  partialPrefetching: false,
};

export default nextConfig;
