/* window.alert를 SweetAlert2로 교체 */
window.alert = function(message, callback) {
    parent.Swal.fire({
      title: message,
      icon: "success"
    }).then(() => {
        if(typeof callback === 'function'){
            callback();
        }
    })
};