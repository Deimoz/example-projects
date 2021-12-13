var currentPig = 0

var pigs = document.getElementsByClassName('pig-card')

function update_photo() {
    let holder = pigs[currentPig].getElementsByClassName("photo-holder")
    holder[0].style.height=document.querySelector('.pig-list').offsetWidth.toString() + "px"
}

window.onload = () => {
    pigs[0].style.display = 'flex'
    pigs[currentPig].style.opacity = '1'
    update_photo()
}

function like_click() {
    nextCard()
}

function dislike_click() {
    nextCard()
}

function nextCard() {
    if (currentPig < pigs.length - 1) {
        pigs[currentPig].style.opacity = '0'
        setTimeout(function() {
            pigs[currentPig].style.display = 'none'
            currentPig++
            if (currentPig < pigs.length) {
                pigs[currentPig].style.display = 'flex'
                setTimeout(function() {
                    pigs[currentPig].style.opacity = '1'
                }, 300);
                update_photo()
            }
        }, 300);
    }
}