// 鍥剧墖鎳掑姞杞芥寚浠?
const lazyLoad = {
  mounted(el, binding) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = el
          const src = binding.value
          
          // 璁剧疆鍔犺浇鐘舵?
          img.classList.add('lazy-loading')
          
          // 鍔犺浇鍥剧墖
          const tempImg = new Image()
          tempImg.onload = () => {
            img.src = src
            img.classList.remove('lazy-loading')
            img.classList.add('lazy-loaded')
          }
          tempImg.onerror = () => {
            img.classList.remove('lazy-loading')
            img.classList.add('lazy-error')
            // 浣跨敤榛樿ゅ浘鐗
            img.src = '/default-image.png'
          }
          tempImg.src = src
          
          // 鍋滄㈣傚療
          observer.unobserve(el)
        }
      })
    }, {
      rootMargin: '50px 0px', // 鎻愬墠50px寮濮嬪姞杞?
      threshold: 0.01
    })
    
    observer.observe(el)
    
    // 淇濆瓨observer浠ヤ究鍗歌浇鏃舵竻鐞?
    el._lazyObserver = observer
  },
  
  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
    }
  }
}

export default lazyLoad
