<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<form class="login" id="login">
  <div class="login-table">
    <label for="phone">手机号</label>
    <input class="input login-phone" name="phone" type="text" title="手机号" />

    <label for="captcha">验证码</label>
    <input class="input" name="captcha" type="text" title="验证码" />
    <button class="button-small login-captcha" id="login-captcha" type="button">获取验证码</button>

    <button class="button login-submit" type="submit">登录/注册</button>
  </div>
</form>
<script type="module">
"use strict";
const messageDialog = (message, callback) => {
  let backdrop = document.getElementById('dialog-backdrop');
  if (backdrop === null) {
    backdrop = document.createElement('div');
    backdrop.classList.add('dialog-backdrop');
    backdrop.id = 'dialog-backdrop';
    document.body.appendChild(backdrop);
    requestAnimationFrame(() => {
      backdrop.style.opacity = '1';
    });
  }
  const dialog = document.createElement('div');
  dialog.classList.add('dialog');
  const innerDiv = document.createElement('div');
  innerDiv.classList.add('dialog-inner');
  const messageParagraph = document.createElement('p');
  messageParagraph.classList.add('dialog-message');
  messageParagraph.textContent = message;
  const actionsDiv = document.createElement('div');
  actionsDiv.classList.add('dialog-actions');
  const confirmButton = document.createElement('button');
  confirmButton.classList.add('button', 'dialog-actions-confirm');
  confirmButton.type = 'button';
  confirmButton.title = '确认';
  confirmButton.textContent = '确认';
  confirmButton.addEventListener('click', () => {
    dialog.remove();
    if (document.querySelectorAll('.dialog').length === 0) {
      backdrop.addEventListener('transitionend', function () {
        backdrop.remove();
      }, { once: true });
      backdrop.style.opacity = '';
    }
    callback?.();
  });
  actionsDiv.appendChild(confirmButton);
  innerDiv.appendChild(messageParagraph);
  innerDiv.appendChild(actionsDiv);
  dialog.appendChild(innerDiv);
  document.body.appendChild(dialog);
};
const loadingDialog = () => {
  const loading = document.createElement('div');
  loading.classList.add('loading');
  const innerDiv = document.createElement('div');
  innerDiv.classList.add('loading-inner');
  const loadingIcon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  loadingIcon.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
  loadingIcon.setAttribute('viewBox', '0 0 200 200');
  loadingIcon.setAttribute('fill', 'currentColor');
  const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
  path.setAttribute('d', 'M105,180c0-2.8-2.2-5-5-5c-41.4,0-75-33.6-75-75s33.6-75,75-75s75,33.6,75,75c0,2.8,2.2,5,5,5s5-2.2,5-5c0-46.9-38.1-85-85-85s-85,38.1-85,85s38.1,85,85,85C102.8,185,105,182.8,105,180z');
  loadingIcon.appendChild(path);
  const messageSpan = document.createElement('span');
  messageSpan.textContent = '加载中，请稍后';
  innerDiv.appendChild(loadingIcon);
  innerDiv.appendChild(messageSpan);
  loading.appendChild(innerDiv);
  document.body.appendChild(loading);
  return {
    dismiss: () => {
      loading.remove();
    }
  };
};
const main = () => {
  const captchaButton = document.getElementById('login-captcha');
  captchaButton?.addEventListener('click', async () => {
    const phoneInput = document.querySelector('input[name="phone"]');
    if (phoneInput === null) {
      return;
    }
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/captcha', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          phone: phoneInput.value
        })
      });
    }
    catch (error) {
      console.error(error);
    }
    if (response === null) {
      return;
    }
    loading.dismiss();
    if (response.ok) {
      const data = await response.json();
      messageDialog(`当然，这个发送验证码是假的，验证码是 \${data.captcha}`);
      captchaButton.disabled = true;
      const originalText = captchaButton.textContent;
      let countdown = 10;
      captchaButton.textContent = `\${originalText} (\${countdown}s)`;
      const interval = setInterval(() => {
        countdown--;
        if (countdown > 0) {
          captchaButton.textContent = `\${originalText} (\${countdown}s)`;
        }
        else {
          clearInterval(interval);
          captchaButton.textContent = originalText;
          captchaButton.disabled = false;
        }
      }, 1000);
    }
    else if (response.status === 400) {
      const data = await response.json();
      messageDialog(`发生错误：\${data.error}`);
    }
    else if (response.status === 401) {
      window.location.href = '/login';
    }
    else {
      console.error(response.status);
    }
  });
  const loginForm = document.getElementById('login');
  loginForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(loginForm);
    const formValues = {};
    formData.forEach((value, key) => {
      formValues[key] = value.toString();
    });
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          phone: formValues['phone'],
          captcha: formValues['captcha']
        })
      });
    }
    catch (error) {
      console.error(error);
    }
    if (response === null) {
      return;
    }
    loading.dismiss();
    if (response.ok) {
      const data = await response.json();
      messageDialog(data.message, () => {
        window.location.href = '/appointment';
      });
    }
    else if (response.status === 400) {
      const data = await response.json();
      messageDialog(`发生错误：\${data.error}`);
    }
    else if (response.status === 401) {
      window.location.href = '/login';
    }
    else {
      console.error(response.status);
    }
  });
};
main();
</script>