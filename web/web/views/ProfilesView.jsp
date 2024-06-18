<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="profile">
  <h1 class="profile-title">信息管理</h1>
  <h2>新增身份</h2>
  <div class="profile-new">
    <form class="profile-new-form" id="profile-new-form">
      <table class="table-small">
        <tbody>
          <tr>
            <td>姓名</td>
            <td><input type="text" class="input" name="name"></td>
          </tr>
          <tr>
            <td>身份证</td>
            <td><input type="text" class="input" name="id_card"></td>
          </tr>
          <tr>
            <td>联系电话</td>
            <td><input type="text" class="input" name="phone"></td>
          </tr>
        </tbody>
      </table>
      <p>
        <input type="checkbox" id="profile-new-agreement">
        <label for="profile-new-agreement">系统不会对身份信息是否正确做出校验，如果填写错误，将会导致不能正常就诊</label>
      </p>
      <button type="submit" class="button profile-new-submit" id="profile-new-submit" title="保存" disabled>保存</button>
    </form>
  </div>
  <h2>已保存的身份</h2>
  <ul class="profile-list" id="profile-list"></ul>
</div>
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
  const profileNewAgreementCheckbox = document.getElementById('profile-new-agreement');
  const profileNewSubmitButton = document.getElementById('profile-new-submit');
  if (profileNewAgreementCheckbox !== null && profileNewSubmitButton !== null) {
    profileNewAgreementCheckbox.addEventListener('change', () => {
      profileNewSubmitButton.disabled = !profileNewAgreementCheckbox.checked;
    });
  }
  const profileNewForm = document.getElementById('profile-new-form');
  profileNewForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(profileNewForm);
    const formValues = {};
    formData.forEach((value, key) => {
      formValues[key] = value.toString();
    });
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/profiles', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: formValues['name'],
          id_card: formValues['id_card'],
          phone: formValues['phone']
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
        refreshProfiles();
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
  const drawProfile = (profile) => {
    const li = document.createElement('li');
    li.className = 'profile-item';
    const div = document.createElement('div');
    const table = document.createElement('table');
    table.className = 'table-small';
    const tbody = document.createElement('tbody');
    const rowData = [
      { label: '姓名', value: profile.name },
      { label: '身份证', value: profile.id_card },
      { label: '联系电话', value: profile.phone }
    ];
    rowData.forEach(data => {
      const tr = document.createElement('tr');
      const tdLabel = document.createElement('td');
      tdLabel.textContent = data.label;
      const tdValue = document.createElement('td');
      tdValue.textContent = data.value;
      tr.appendChild(tdLabel);
      tr.appendChild(tdValue);
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    div.appendChild(table);
    const button = document.createElement('button');
    button.className = 'button profile-item-delete';
    button.title = '删除';
    button.textContent = '删除';
    button.addEventListener('click', async () => {
      const loading = loadingDialog();
      let response = null;
      try {
        response = await fetch('/api/profiles', {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            id: profile.id
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
        li.remove();
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
    li.appendChild(div);
    li.appendChild(button);
    return li;
  };
  const refreshProfiles = async () => {
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/profiles', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
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
      const profiles = await response.json();
      const list = document.getElementById('profile-list');
      if (list === null) {
        return;
      }
      while (list.firstChild) {
        list.removeChild(list.firstChild);
      }
      profiles.forEach(profile => {
        list.appendChild(drawProfile(profile));
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
  };
  refreshProfiles();
};
main();
</script>