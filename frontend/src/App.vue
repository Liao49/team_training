<template>
  <el-container class="layout">
    <el-header class="header">
      <span class="title">用户管理系统</span>
      <span class="sub">团队规范实训 · 前后端分离示例</span>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<style>
body { margin: 0; background: #f5f7fa; }
.layout { min-height: 100vh; }
.header {
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
}
.title { font-size: 20px; font-weight: 600; }
.sub { font-size: 13px; opacity: 0.85; }
</style>
