<template>
  <div class="app-container">
    <aside class="sidebar-left">
      <div class="sidebar-section">
        <h2>💜 AI 数字陪伴</h2>
        <p class="session-id">当前会话：{{ sessionId }}</p>
      </div>
      <div class="sidebar-section">
        <h3>控制台</h3>
        <button @click="createSession" class="btn-new">✨ 新建会话</button>
        <button @click="openCallModal" class="btn-call">📞 打电话</button>
        <div class="voice-switch">
          <label><input type="checkbox" v-model="autoSpeak" />自动语音播报</label>
        </div>
      </div>
      <div class="sidebar-section">
        <h3>会话历史</h3>
        <div class="session-list">
          <div v-for="s in sessions" :key="s" class="session-item" :class="{ active: s === sessionId }" @click="loadSession(s)">
            <span class="session-name">{{ s }}</span>
            <button @click.stop="delSession(s)" class="btn-delete">🗑</button>
          </div>
        </div>
      </div>
    </aside>

    <main class="chat-main">
      <div class="chat-header">
        <div class="stat-badge">消息数：{{ messageList.length }}</div>
        <div class="stat-badge" :style="{color: voiceId ? '#53d8fb' : '#b7bae8'}">
          音色：{{ voiceId ? '已克隆' : '预设音色' }}
        </div>
        <div class="stat-badge" style="color:#ffca28">
          当前情绪：{{ currentEmotion }}
        </div>
      </div>

      <div class="chat-box" ref="chatBox">
        <div v-for="(msg, i) in messageList" :key="i" class="msg-bubble" :class="msg.role">
          <div class="msg-content">{{ msg.content }}</div>
          <button v-if="msg.role === 'assistant'" @click.stop="speakText(msg.content)" class="speak-btn">🔊</button>
        </div>
      </div>

      <div class="input-area">
        <button @click="toggleVoice" class="voice-btn" :class="{ recording: isRecording }">
          {{ isRecording ? '🛑 停止' : '🎤 语音' }}
        </button>
        <input v-model="inputText" placeholder="说点什么...（或点击语音输入）" @keydown.enter.prevent="send" class="chat-input" />
        <button @click="send" class="send-btn">发送</button>
      </div>
    </main>

    <aside class="sidebar-right">
      <div class="setting-card">
        <h3>伴侣设置</h3>
        <div class="form-group">
          <label>昵称</label>
          <input v-model="nickName" placeholder="输入昵称" />
        </div>
        <div class="form-group">
          <label>性格</label>
          <textarea v-model="nature" placeholder="输入性格描述"></textarea>
        </div>

        <div class="form-group">
          <label style="color: #53d8fb;">🎙️ 音色克隆</label>
          <div style="display: flex; gap: 8px; margin: 8px 0;">
            <button @click="startCloneRecord" class="clone-btn" :class="{ recording: isCloneRecording }">
              {{ isCloneRecording ? '🛑 结束录音' : '🔴 录音克隆' }}
            </button>
            <label class="clone-btn upload-btn">
              📁 上传音频
              <input type="file" ref="fileInput" accept="audio/*" hidden @change="uploadCloneVoice" />
            </label>
          </div>
          <p style="font-size: 12px; color: #ecedff; opacity: 0.8;">要求：3-60秒 / 清晰人声 / 无背景音乐</p>
        </div>

            <div class="form-group">
              <label>🎵 预设音色（免费可用）</label>
              <select v-model.number="ttsPer" class="select-input">
              <option :value="0">度小美（温柔女声 ✅）</option>
              <option :value="1">度小宇（阳光男声）</option>
              <option :value="3">度逍遥（成熟男声）</option>
              <option :value="4">度丫丫（可爱童声）</option>
              </select>
            </div>

        <div class="form-group">
          <label>⏱️ 语速：{{ ttsSpd }}</label>
          <input type="range" v-model.number="ttsSpd" min="0" max="15" class="slider" />
        </div>

        <div class="form-group">
          <label>🎼 音调：{{ ttsPit }}</label>
          <input type="range" v-model.number="ttsPit" min="0" max="15" class="slider" />
        </div>

        <div class="form-group">
          <label>🔊 音量：{{ ttsVol }}</label>
          <input type="range" v-model.number="ttsVol" min="0" max="15" class="slider" />
        </div>

        <div class="form-group">
          <label>😊 情感风格（仅度逍遥支持）</label>
          <select v-model="ttsEmotion" class="select-input">
            <option value="">默认</option>
            <option value="happy">开心甜美</option>
            <option value="sad">温柔亲切</option>
            <option value="angry">撒娇可爱</option>
            <option value="calm">平静温和</option>
          </select>
        </div>

        <button @click="saveProfile" class="btn-save">💾 保存设置</button>
      </div>
    </aside>

  </div>

  <div v-show="showCallModal" class="call-modal-overlay">
    <div class="call-modal" :style="{ left: modalLeft + 'px', top: modalTop + 'px' }" @mousedown="startDrag">
      <button @click.stop="closeCallModal" class="call-close">✕</button>
      
      <div class="face-camera-box">
        <video ref="videoRef" autoplay muted playsinline class="face-video"></video>
        <canvas ref="canvasRef" class="face-canvas"></canvas>
        <div class="emotion-tag">{{ currentEmotion }}</div>
      </div>

      <div class="avatar-container">
        <div class="virtual-avatar" :class="{ talking: isAiTalking }"></div>
        <div class="avatar-name">{{ nickName }}</div>
      </div>

      <div class="call-status">
        {{ isCallRecording ? '🎙️ 正在听你说话...' : (isAiTalking ? '🗣️ AI正在回复...' : '📞 等待通话') }}
      </div>

      <button v-if="isAiTalking" @click.stop="interrupt" class="interrupt-btn">⏸️ 打断</button>
      <button @click.stop="closeCallModal" class="hangup-btn">📞 挂断</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, onUnmounted } from 'vue'
import axios from 'axios'

const request = axios.create({ baseURL: 'http://127.0.0.1:8000/api' })

// 基础变量
const sessionId = ref('')
const nickName = ref('小甜甜')
const nature = ref('温柔体贴的伴侣')
const messageList = ref([])
const inputText = ref('')
const sessions = ref([])
const chatBox = ref(null)
const autoSpeak = ref(true)

// 语音合成参数（统一数字类型）
const voiceId = ref('')
const ttsPer = ref(4) // 默认：度小美
const ttsSpd = ref(4)
const ttsPit = ref(6)
const ttsVol = ref(5)
const ttsEmotion = ref('')

// 音色克隆
const isCloneRecording = ref(false)
const mediaRecorder = ref(null)
const audioChunks = ref([])
const fileInput = ref(null)

// 语音转文字
const isRecording = ref(false)
const recognition = ref(null)
const recogScene = ref('chat')

// 通话弹窗
const showCallModal = ref(false)
const isCallRecording = ref(false)
const isAiTalking = ref(false)
let currentAudio = null

// 拖拽
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const modalLeft = ref(0)
const modalTop = ref(0)

// 情绪识别
const videoRef = ref(null)
const canvasRef = ref(null)
const currentEmotion = ref('中性')
const userEmotion = ref('neutral')
const emotionTtsParams = ref({ spd: 4, pit: 6, emotion: '' })
let animationId = null
let lastNoFaceLogAt = 0
let lastDetectAt = 0
const DETECT_INTERVAL_MS = 300

const MODEL_URL = 'https://cdn.jsdelivr.net/gh/justadudewhohacks/face-api.js@0.22.2/weights/'

const emotionLabelMap = {
  angry: 'angry', disgusted: 'angry', fearful: 'sad', happy: 'happy', sad: 'sad', surprised: 'surprise', neutral: 'neutral'
}
const emotionDescMap = {
  angry: '平静温和（安抚）', disgusted: '平静温和（安抚）', fearful: '温柔亲切', happy: '开心甜美', sad: '温柔亲切', surprised: '惊喜活泼', neutral: '中性'
}

function emotionToTts(emotion) {
  const emotionMap = {
    happy: { spd: 6, pit: 7, emotion: 'happy', desc: '开心甜美' },
    sad: { spd: 3, pit: 4, emotion: 'sad', desc: '温柔亲切' },
    angry: { spd: 4, pit: 3, emotion: 'calm', desc: '平静温和（安抚）' },
    surprise: { spd: 7, pit: 8, emotion: 'happy', desc: '惊喜活泼' },
    neutral: { spd: 4, pit: 6, emotion: '', desc: '中性' }
  }
  const cfg = emotionMap[emotion] || emotionMap.neutral
  cfg.spd = Math.max(0, Math.min(15, cfg.spd))
  cfg.pit = Math.max(0, Math.min(15, cfg.pit))
  return cfg
}

async function detectEmotion() {
  if (!showCallModal.value || !videoRef.value) {
    cancelAnimationFrame(animationId)
    animationId = null
    return
  }
  const now = Date.now()
  if (now - lastDetectAt < DETECT_INTERVAL_MS) {
    animationId = requestAnimationFrame(detectEmotion)
    return
  }
  lastDetectAt = now

  try {
    const detections = await faceapi.detectAllFaces(
      videoRef.value, new faceapi.TinyFaceDetectorOptions({ inputSize: 224, scoreThreshold: 0.25 })
    ).withFaceExpressions()

    if (detections.length > 0) {
      const expressions = detections[0].expressions
      const dominant = Object.entries(expressions).reduce((a, b) => a[1] > b[1] ? a : b)
      const emotionKey = dominant[0]
      const mappedEmotion = emotionLabelMap[emotionKey] || 'neutral'
      currentEmotion.value = emotionDescMap[emotionKey] || '中性'
      userEmotion.value = mappedEmotion
      emotionTtsParams.value = emotionToTts(mappedEmotion)
    } else {
      currentEmotion.value = '中性'
      userEmotion.value = 'neutral'
      emotionTtsParams.value = emotionToTts('neutral')
    }
  } catch (e) {
    console.error('情绪检测失败：', e)
  }
  animationId = requestAnimationFrame(detectEmotion)
}

async function startVideo() {
  try {
    lastDetectAt = 0
    const stream = await navigator.mediaDevices.getUserMedia({ video: true })
    videoRef.value.srcObject = stream
    await videoRef.value.play()
    await nextTick()
    canvasRef.value.width = videoRef.value.videoWidth
    canvasRef.value.height = videoRef.value.videoHeight

    await faceapi.nets.tinyFaceDetector.loadFromUri(MODEL_URL)
    await faceapi.nets.faceExpressionNet.loadFromUri(MODEL_URL)
    detectEmotion()
  } catch (e) {
    console.error('摄像头启动失败：', e)
  }
}

function cleanTextForTTS(text) {
  return text.replace(/\([^)]*\)/g, '').replace(/\s+/g, ' ').trim()
}

// 初始化语音识别
function initVoiceRecognition() {
  if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
    alert('浏览器不支持语音输入，请使用Chrome/Edge最新版')
    return false
  }
  if (recognition.value) return true
  
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  recognition.value = new SpeechRecognition()
  recognition.value.lang = 'zh-CN'
  recognition.value.continuous = false
  recognition.value.interimResults = false

  recognition.value.onerror = (e) => {
    console.error('语音识别错误：', e.error)
    forceStopRec()
    e.error === 'not-allowed' && alert('请允许麦克风权限！')
  }
  return true
}

function forceStopRec(){
  if(!recognition.value) return
  recognition.value.onresult = recognition.value.onend = null
  recognition.value.stop()
  isRecording.value = false
}

function startVoice() {
  if (!initVoiceRecognition()) return
  forceStopRec()
  recogScene.value = 'chat'
  isRecording.value = true
  recognition.value.onresult = (e) => {
    inputText.value = e.results[0][0].transcript
    forceStopRec()
  }
  recognition.value.onend = forceStopRec
  recognition.value.start()
}

function toggleVoice() {
  isRecording.value ? forceStopRec() : startVoice()
}

function startCallVoice() {
  if (!initVoiceRecognition()) return
  forceStopRec()
  recogScene.value = 'call'
  isCallRecording.value = true
  isAiTalking.value = false

  recognition.value.onresult = async (e) => {
    const userText = e.results[0][0].transcript
    stopCallVoice()
    await sendCallMessage(userText)
  }

  recognition.value.onend = () => {
    showCallModal.value && !isCallRecording.value && !isAiTalking.value && startCallVoice()
  }
  recognition.value.start()
}

function stopCallVoice() {
  if (!recognition.value) return
  isCallRecording.value = false
  recognition.value.stop()
}

async function speakText(text) {
  try {
    currentAudio?.pause()
    window.speechSynthesis.cancel()
    isAiTalking.value = true

    const useSpd = showCallModal.value ? emotionTtsParams.value.spd : ttsSpd.value
    const usePit = showCallModal.value ? emotionTtsParams.value.pit : ttsPit.value

    const params = { 
      text, 
      spd: useSpd, 
      pit: usePit, 
      vol: ttsVol.value 
    }
    
    // 克隆音色 > 手动选中的预设音色
    if (voiceId.value) {
      params.voice_id = voiceId.value
    } else {
      params.per = ttsPer.value  // 强制拿你当前下拉框选的值！！
    }

    const { data } = await request.get("/baidu-tts", { params })
    currentAudio = new Audio(data.audio_url)
    currentAudio.onerror = () => speakBrowserFallback(text)
    currentAudio.onended = () => {
      isAiTalking.value = false
      currentAudio = null
      showCallModal.value && startCallVoice()
    }
    currentAudio.play()
  } catch (e) { speakBrowserFallback(text) }
}
function speakBrowserFallback(text) {
  isAiTalking.value = true
  const u = new SpeechSynthesisUtterance(cleanTextForTTS(text))
  u.rate = ttsSpd.value/5
  u.pitch = ttsPit.value/5
  u.volume = ttsVol.value/15
  u.onend = () => {
    isAiTalking.value = false
    showCallModal.value && startCallVoice()
  }
  window.speechSynthesis.speak(u)
}

function interrupt() {
  currentAudio?.pause()
  window.speechSynthesis.cancel()
  isAiTalking.value = false
  showCallModal.value && startCallVoice()
}

// 音色克隆
async function startCloneRecord() {
  if (isCloneRecording.value) {
    mediaRecorder.value.stop()
    isCloneRecording.value = false
    alert('录音结束！正在上传克隆...')
    return
  }
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder.value = new MediaRecorder(stream, MediaRecorder.isTypeSupported('audio/wav')?{mimeType:'audio/wav'}:{mimeType:'audio/mp4'})
    audioChunks.value = []
    mediaRecorder.value.ondataavailable = (e) => audioChunks.value.push(e.data)
    mediaRecorder.value.onstop = handleCloneRecordFinish
    mediaRecorder.value.start()
    isCloneRecording.value = true
    alert('开始录音！3-60秒清晰人声')
  } catch (err) { alert('麦克风授权失败：' + err.message) }
}
function handleCloneRecordFinish() {
  const formData = new FormData()
  formData.append('file', new Blob(audioChunks.value, {type:mediaRecorder.value.mimeType}), 'clone_audio')
  submitCloneVoice(formData)
}
function uploadCloneVoice(e) {
  const formData = new FormData()
  formData.append('file', e.target.files[0])
  submitCloneVoice(formData)
}
async function submitCloneVoice(formData) {
  try {
    const { data } = await request.post('/clone-voice', formData)
    voiceId.value = data.voice_id
    alert(data.msg)
    await saveProfile()
  } catch (e) { alert('克隆失败：' + (e.response?.data?.detail || e.message)) }
}

// 初始化
onMounted(async () => {
  try {
    const { data } = await request.get('/new-session')
    sessionId.value = data.session_id
    await getSessions()
  } catch (e) {
    console.error('初始化失败:', e)
    alert('初始化失败：无法连接后端')
  } finally {
    initVoiceRecognition()
  }
})
async function getSessions() { const { data } = await request.get('/sessions'); sessions.value = data.sessions }

// 发送消息
async function send() {
  forceStopRec()
  if (!inputText.value.trim()) return
  const text = inputText.value
  inputText.value = ''
  messageList.value.push({ role: 'user', content: text })
  await nextTick()
  chatBox.value.scrollTop = chatBox.value.scrollHeight

  try {
    const { data } = await request.post('/chat', {
      session_id: sessionId.value,
      nick_name: nickName.value,
      nature: nature.value,
      message: messageList.value,
      user_emotion: userEmotion.value,
      voice_id: voiceId.value,
      tts_per: ttsPer.value,
      tts_spd: ttsSpd.value,
      tts_pit: ttsPit.value,
      tts_vol: ttsVol.value,
      tts_emotion: ttsEmotion.value
    })
    messageList.value.push({ role: 'assistant', content: data.reply })
    autoSpeak.value && speakText(data.reply)
    await saveProfile()
  } catch (e) { alert('请求失败：' + (e.response?.data?.detail || e.message)) }
}

// 会话管理
async function createSession() {
  const { data } = await request.get('/new-session')
  sessionId.value = data.session_id
  messageList.value = []
  voiceId.value = ''
  getSessions()
}
async function loadSession(id) {
  forceStopRec()
  interrupt()
  const { data } = await request.post('/load-session', { session_id: id })
  sessionId.value = data.session_id
  nickName.value = data.nick_name
  nature.value = data.nature
  messageList.value = data.message
  voiceId.value = data.voice_id || ''
  ttsPer.value = data.tts_per || 4
  ttsSpd.value = data.tts_spd || 4
  ttsPit.value = data.tts_pit || 6
  ttsVol.value = data.tts_vol || 5
  ttsEmotion.value = data.tts_emotion || ''
}
async function delSession(id) {
  await request.post('/delete-session', { session_id: id })
  getSessions()
  sessionId.value === id && createSession()
}
async function saveProfile() {
  await request.post('/save-session', {
    session_id: sessionId.value,
    nick_name: nickName.value,
    nature: nature.value,
    message: messageList.value,
    voice_id: voiceId.value,
    tts_per: ttsPer.value,
    tts_spd: ttsSpd.value,
    tts_pit: ttsPit.value,
    tts_vol: ttsVol.value,
    tts_emotion: ttsEmotion.value
  })
}

// 打开通话
function openCallModal() {
  forceStopRec()
  showCallModal.value = true
  nextTick(async () => {
    modalLeft.value = (window.innerWidth - 380) / 2
    modalTop.value = (window.innerHeight - 520) / 2
    await startVideo()
    startCallVoice()
  })
}

// 关闭通话
function closeCallModal() {
  showCallModal.value = false
  isCallRecording.value = isAiTalking.value = false
  currentEmotion.value = '中性'
  userEmotion.value = 'neutral'
  forceStopRec()
  window.speechSynthesis.cancel()
  currentAudio?.pause()
  
  if (videoRef.value?.srcObject) {
    videoRef.value.srcObject.getTracks().forEach(t => t.stop())
  }
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
}

// 通话消息
async function sendCallMessage(text) {
  try {
    const dialogHistory = [...messageList.value, { role: 'user', content: text }]
    const { data } = await request.post('/chat', {
      session_id: sessionId.value,
      nick_name: nickName.value,
      nature: nature.value,
      message: dialogHistory,
      user_emotion: userEmotion.value,
      voice_id: voiceId.value,
      tts_per: ttsPer.value,
      tts_spd: emotionTtsParams.value.spd,
      tts_pit: emotionTtsParams.value.pit,
      tts_vol: ttsVol.value,
      tts_emotion: emotionTtsParams.value.emotion
    })
    messageList.value = [...dialogHistory, { role: 'assistant', content: data.reply }]
    speakText(data.reply)
    await saveProfile()
  } catch (e) { alert('通话失败：' + e.response?.data?.detail); closeCallModal() }
}

// 拖拽
function startDrag(e) {
  if (e.button !== 0) return
  isDragging.value = true
  dragStartX.value = e.clientX
  dragStartY.value = e.clientY
  document.addEventListener('mousemove', drag)
  document.addEventListener('mouseup', stopDrag)
}
function drag(e) {
  if (!isDragging.value) return
  modalLeft.value += e.clientX - dragStartX.value
  modalTop.value += e.clientY - dragStartY.value
  dragStartX.value = e.clientX
  dragStartY.value = e.clientY
}
function stopDrag() {
  isDragging.value = false
  document.removeEventListener('mousemove', drag)
  document.removeEventListener('mouseup', stopDrag)
}

watch(messageList, () => nextTick(() => chatBox.value.scrollTop = chatBox.value.scrollHeight))
onUnmounted(() => { forceStopRec(); closeCallModal(); stopDrag() })
</script>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Segoe UI', sans-serif; }
.app-container { height: 100vh; display: flex; background: linear-gradient(135deg, #0f1020 0%, #1a1b3a 100%); color: #fff; overflow: hidden; }
.sidebar-left { width: 280px; background: rgba(138, 123, 255, 0.2); border-right: 1px solid rgba(255,255,255,0.1); padding: 24px 16px; display: flex; flex-direction: column; gap: 24px; backdrop-filter: blur(10px); }
.sidebar-section { display: flex; flex-direction: column; gap: 12px; }
.sidebar-left h2 { font-size: 1.5rem; color: #fff; }
.session-id { color: #b7bae8; font-size: 0.85rem; }
.sidebar-left h3 { font-size: 1.1rem; color: #ecedff; }
.btn-new { background: linear-gradient(135deg, #8a7bff, #7a6bff); color: white; border: none; padding: 10px 16px; border-radius: 12px; cursor: pointer; font-weight: 600; }
.btn-call { background: linear-gradient(135deg, #4facfe, #00f2fe); color: white; border: none; padding: 10px 16px; border-radius: 12px; cursor: pointer; font-weight: 600; margin-top: 8px;}
.voice-switch { font-size: 0.9rem; color: #ecedff; }
.session-list { display: flex; flex-direction: column; gap: 8px; max-height: 300px; overflow-y: auto; }
.session-item { display: flex; justify-content: space-between; align-items: center; background: rgba(255,255,255,0.1); padding: 10px 12px; border-radius: 10px; cursor: pointer; }
.session-item.active { background: rgba(138, 123, 255, 0.6); }
.btn-delete { background: transparent; border: none; color: #ff6b6b; cursor: pointer; }
.chat-main { flex: 1; display: flex; flex-direction: column; padding: 20px; gap: 16px; }
.chat-header { display: flex; gap: 16px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.1); }
.stat-badge { background: rgba(255,255,255,0.15); padding: 6px 12px; border-radius: 20px; font-size: 0.9rem; color: #ecedff; }
.chat-box { flex: 1; padding: 10px; overflow-y: auto; display: flex; flex-direction: column; gap: 12px; }
.msg-bubble { max-width: 75%; padding: 14px 18px; border-radius: 18px; display: flex; align-items: center; gap: 8px; }
.msg-bubble.user { background: #fff; align-self: flex-end; border-bottom-right-radius: 4px; }
.msg-bubble.assistant { background: linear-gradient(135deg, #8a7bff, #7a6bff); align-self: flex-start; border-bottom-left-radius: 4px; }
.msg-content { font-size: 1rem; line-height: 1.5; flex: 1; }
.msg-bubble.user .msg-content { color: #111; }
.msg-bubble.assistant .msg-content { color: #fff; }
.speak-btn { background: rgba(255,255,255,0.2); border: none; border-radius: 50%; width: 28px; height: 28px; font-size: 14px; cursor: pointer; color: #fff; }
.input-area { display: flex; gap: 12px; align-items: center; background: rgba(255,255,255,0.08); padding: 12px; border-radius: 16px; }
.voice-btn { padding: 12px 14px; background: linear-gradient(135deg, #53d8fb, #42c7e9); color: white; border: none; border-radius: 14px; cursor: pointer; font-weight: 600; }
.voice-btn.recording { background: linear-gradient(135deg, #ff6b6b, #ff4444); animation: pulse 1s infinite; }
.chat-input { flex: 1; padding: 12px 16px; border: 1px solid rgba(255,255,255,0.2); border-radius: 14px; background: rgba(255,255,255,0.9); color: #111; }
.send-btn { padding: 12px 20px; background: linear-gradient(135deg, #8a7bff, #7a6bff); color: white; border: none; border-radius: 14px; cursor: pointer; font-weight: 600; }
.sidebar-right { width: 320px; background: rgba(138, 123, 255, 0.15); border-left: 1px solid rgba(255,255,255,0.1); padding: 24px 16px; backdrop-filter: blur(10px); }
.setting-card { background: rgba(255,255,255,0.1); padding: 20px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.2); }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 0.9rem; color: #ecedff; }
.form-group input, .form-group textarea { width: 100%; padding: 10px 12px; border: 1px solid rgba(255,255,255,0.2); border-radius: 10px; background: #fff; color: #111; }
.select-input { width: 100%; padding: 8px 12px; border-radius: 8px; border: none; margin-top: 4px; }
.slider { width: 100%; margin-top: 4px; accent-color: #53d8fb; }
.btn-save { width: 100%; background: linear-gradient(135deg, #53d8fb, #42c7e9); color: white; border: none; padding: 12px; border-radius: 10px; cursor: pointer; font-weight: 600; }
.clone-btn { flex: 1; padding: 8px 10px; border: none; border-radius: 8px; color: white; cursor: pointer; font-size: 12px; background: linear-gradient(135deg, #53d8fb, #42c7e9); }
.clone-btn.recording { background: linear-gradient(135deg, #ff6b6b, #ff4444); animation: pulse 1s infinite; }
.upload-btn { background: linear-gradient(135deg, #8a7bff, #7a6bff); cursor: pointer; }
@keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.05); } 100% { transform: scale(1); } }

.call-modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.7); z-index: 9999; }
.call-modal { width: 380px; height: 520px; background: linear-gradient(135deg, #2d1b69, #4a3a9c); border-radius: 24px; padding: 24px; text-align: center; position: absolute; box-shadow: 0 0 30px rgba(138,123,255,0.5); user-select: none; cursor: move; }
.call-close { position: absolute; top: 16px; right: 16px; background: rgba(255,255,255,0.2); border: none; width: 32px; height: 32px; border-radius: 50%; color: white; font-size: 18px; cursor: pointer; z-index: 10; }

.face-camera-box {
  position: relative;
  width: 160px;
  height: 120px;
  margin: 0 auto 8px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #53d8fb;
}
.face-video, .face-canvas {
  position: absolute;
  left: 0; top: 0;
  width: 100%; height: 100%;
  object-fit: cover;
}
.emotion-tag {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.6);
  color: #fff;
  font-size: 12px;
  padding: 2px;
}

.avatar-container { margin: 12px 0; }
.virtual-avatar { width: 120px; height: 120px; margin: 0 auto; background: linear-gradient(135deg, #8a7bff, #53d8fb); border-radius: 50%; transition: all 0.3s ease; }
.virtual-avatar.talking { animation: avatarTalk 0.5s infinite alternate; }
@keyframes avatarTalk { 0% { transform: scale(1); } 100% { transform: scale(1.08); } }
.avatar-name { margin-top: 12px; font-size: 20px; font-weight: bold; color: #fff; }
.call-status { font-size: 16px; color: #ecedff; margin: 16px 0; height: 24px; }
.interrupt-btn { background: linear-gradient(135deg, #ffca28, #ffb300); color: white; border: none; padding: 10px 24px; border-radius: 50px; font-size: 14px; font-weight: bold; cursor: pointer; margin: 8px 0; }
.hangup-btn { background: linear-gradient(135deg, #ff6b6b, #ff4444); color: white; border: none; padding: 14px 32px; border-radius: 50px; font-size: 16px; font-weight: bold; cursor: pointer; margin-top: 8px; }
</style>
