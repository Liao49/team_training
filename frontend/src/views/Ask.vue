<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { askApi } from '@/api/user'

const router = useRouter()
const question = ref('')
const answer = ref('')
const sources = ref([])
const loading = ref(false)
const asked = ref(false)

const samples = [
  '《团队规范项目实训》的实验五要求我们完成哪些内容？',
  '课程的实践目标围绕哪四个维度设定？',
  '使用大模型 API 时有哪些安全要求？'
]

async function ask(q) {
  const text = (typeof q === 'string' ? q : question.value).trim()
  if (!text || loading.value) return
  loading.value = true
  asked.value = true
  answer.value = ''
  sources.value = []
  question.value = text
  try {
    const data = await askApi.ask(text)            // 拦截器已解包，直接是 {answer, sources}
    answer.value = data.answer || ''
    sources.value = data.sources || []
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <h2 class="title">学习搭子 · 课程智能问答</h2>
      <el-button style="margin-left: auto" @click="router.push('/')">返回用户管理</el-button>
    </div>
    <p class="hint">
      基于《团队规范项目实训》课程实践方案检索作答（RAG）：回答末尾的 [n] 为资料出处，
      点击"引用出处"可查看命中的原文；资料中没有的内容会明确说明，不做编造。
    </p>

    <div class="samples">
      <span v-for="s in samples" :key="s" class="sample" @click="ask(s)">{{ s }}</span>
    </div>

    <el-input v-model="question" type="textarea" :rows="3" maxlength="500" show-word-limit
              placeholder="输入关于本课程的问题，例如：实验报告需要包含哪些内容？" />

    <div class="actions">
      <el-button type="primary" :loading="loading" @click="ask()">
        {{ loading ? '检索资料并思考中…' : '提　问' }}
      </el-button>
    </div>

    <el-divider />

    <div v-if="loading" v-loading="true" element-loading-text="正在检索课程资料并生成回答…"
         class="loading-box" />
    <div v-else-if="answer" class="answer-box">
      <h3>回答</h3>
      <p class="answer">{{ answer }}</p>
      <h3>引用出处（top-{{ sources.length }} 命中资料）</h3>
      <el-collapse>
        <el-collapse-item v-for="(s, i) in sources" :key="s.id"
                          :title="`[${i + 1}] 资料块 #${s.id}`" :name="s.id">
          <pre class="source-text">{{ s.text }}</pre>
        </el-collapse-item>
      </el-collapse>
    </div>
    <el-empty v-else-if="asked" description="暂无回答" />
  </el-card>
</template>

<style scoped>
.toolbar { margin-bottom: 12px; display: flex; align-items: center; }
.title { margin: 0; font-size: 18px; }
.hint { margin: 0 0 12px; color: #909399; font-size: 12.5px; line-height: 1.7; }
.samples { margin-bottom: 10px; display: flex; flex-wrap: wrap; gap: 8px; }
.sample {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  padding: 0 10px;
  height: 26px;
  font-size: 12.5px;
  color: #409eff;
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  transition: color .2s, border-color .2s;
}
.sample:hover { color: #79bbff; border-color: #b3d8ff; }
.actions { margin-top: 10px; }
.loading-box { height: 120px; }
.answer-box h3 { margin: 10px 0 8px; font-size: 15px; }
.answer { white-space: pre-wrap; line-height: 1.8; margin: 0 0 18px; }
.source-text { white-space: pre-wrap; font-family: inherit; line-height: 1.7;
               margin: 0; background: #f7f8fa; padding: 10px; border-radius: 4px; }
</style>
