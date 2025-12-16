// 对话框可调整大小指令
export default {
  mounted(el) {
    // 等待 DOM 渲染完成
    setTimeout(() => {
      const dialogElement = el.querySelector('.el-dialog')
      if (!dialogElement) {
        console.warn('未找到 .el-dialog 元素')
        return
      }

      // 设置对话框样式
      dialogElement.style.position = 'relative'
      dialogElement.style.margin = '0 auto'
      
      // 创建右下角调整大小手柄
      const resizer = document.createElement('div')
      resizer.className = 'dialog-resizer'
      resizer.innerHTML = '<svg viewBox="0 0 16 16" style="width: 16px; height: 16px;"><path d="M 14 2 L 2 14 M 16 8 L 8 16 M 16 0 L 0 16" stroke="#409eff" stroke-width="1.5" fill="none"/></svg>'
      resizer.style.cssText = `
        position: absolute;
        right: 4px;
        bottom: 4px;
        width: 20px;
        height: 20px;
        cursor: nwse-resize;
        z-index: 10000;
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0.6;
        transition: opacity 0.3s;
      `
      
      resizer.addEventListener('mouseenter', () => {
        resizer.style.opacity = '1'
      })
      
      resizer.addEventListener('mouseleave', () => {
        resizer.style.opacity = '0.6'
      })
      
      dialogElement.appendChild(resizer)

      // 右下角拖拽调整大小
      let isResizing = false
      let startX, startY, startWidth, startHeight

      resizer.addEventListener('mousedown', (e) => {
        e.preventDefault()
        e.stopPropagation()
        
        isResizing = true
        startX = e.clientX
        startY = e.clientY
        startWidth = dialogElement.offsetWidth
        startHeight = dialogElement.offsetHeight
        
        document.addEventListener('mousemove', onMouseMove)
        document.addEventListener('mouseup', onMouseUp)
        document.body.style.userSelect = 'none'
        document.body.style.cursor = 'nwse-resize'
      })

      function onMouseMove(e) {
        if (!isResizing) return
        
        const deltaX = e.clientX - startX
        const deltaY = e.clientY - startY
        
        const newWidth = Math.max(600, Math.min(window.innerWidth * 0.95, startWidth + deltaX))
        const newHeight = Math.max(400, Math.min(window.innerHeight * 0.9, startHeight + deltaY))
        
        dialogElement.style.width = newWidth + 'px'
        dialogElement.style.height = newHeight + 'px'
      }

      function onMouseUp() {
        isResizing = false
        document.removeEventListener('mousemove', onMouseMove)
        document.removeEventListener('mouseup', onMouseUp)
        document.body.style.userSelect = ''
        document.body.style.cursor = ''
      }

      // 创建四个边缘的拖拽区域
      const createEdge = (position, cursor, styles) => {
        const edge = document.createElement('div')
        edge.className = `dialog-edge-${position}`
        edge.style.cssText = `
          position: absolute;
          z-index: 9999;
          ${styles}
          cursor: ${cursor};
        `
        
        edge.addEventListener('mousedown', (e) => {
          e.preventDefault()
          e.stopPropagation()
          
          const startX = e.clientX
          const startY = e.clientY
          const startWidth = dialogElement.offsetWidth
          const startHeight = dialogElement.offsetHeight
          const rect = dialogElement.getBoundingClientRect()
          
          const onMove = (moveEvent) => {
            const deltaX = moveEvent.clientX - startX
            const deltaY = moveEvent.clientY - startY
            
            let newWidth = startWidth
            let newHeight = startHeight
            
            if (position.includes('right')) {
              newWidth = Math.max(600, Math.min(window.innerWidth * 0.95, startWidth + deltaX))
            }
            if (position.includes('left')) {
              newWidth = Math.max(600, Math.min(window.innerWidth * 0.95, startWidth - deltaX))
            }
            if (position.includes('bottom')) {
              newHeight = Math.max(400, Math.min(window.innerHeight * 0.9, startHeight + deltaY))
            }
            if (position.includes('top')) {
              newHeight = Math.max(400, Math.min(window.innerHeight * 0.9, startHeight - deltaY))
            }
            
            dialogElement.style.width = newWidth + 'px'
            dialogElement.style.height = newHeight + 'px'
          }
          
          const onUp = () => {
            document.removeEventListener('mousemove', onMove)
            document.removeEventListener('mouseup', onUp)
            document.body.style.userSelect = ''
            document.body.style.cursor = ''
          }
          
          document.body.style.userSelect = 'none'
          document.body.style.cursor = cursor
          document.addEventListener('mousemove', onMove)
          document.addEventListener('mouseup', onUp)
        })
        
        dialogElement.appendChild(edge)
      }

      // 创建8个拖拽区域
      createEdge('top', 'ns-resize', 'top: 0; left: 0; right: 0; height: 6px;')
      createEdge('bottom', 'ns-resize', 'bottom: 0; left: 0; right: 0; height: 6px;')
      createEdge('left', 'ew-resize', 'left: 0; top: 0; bottom: 0; width: 6px;')
      createEdge('right', 'ew-resize', 'right: 0; top: 0; bottom: 0; width: 6px;')
      createEdge('top-left', 'nwse-resize', 'top: 0; left: 0; width: 12px; height: 12px;')
      createEdge('top-right', 'nesw-resize', 'top: 0; right: 0; width: 12px; height: 12px;')
      createEdge('bottom-left', 'nesw-resize', 'bottom: 0; left: 0; width: 12px; height: 12px;')
      createEdge('bottom-right', 'nwse-resize', 'bottom: 0; right: 0; width: 12px; height: 12px;')
      
      console.log('对话框调整大小功能已启用')
    }, 100)
  }
}
