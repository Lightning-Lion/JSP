<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="history">
  <h1>挂号历史</h1>
  <h2>生效中</h2>
  <ul class="history-list" id="history-list-available"></ul>
  <h2>已失效</h2>
  <ul class="history-list" id="history-list-unavailable"></ul>
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
  const drawAppointment = (appointment) => {
    const li = document.createElement('li');
    li.className = 'history-item';
    const table = document.createElement('table');
    table.className = 'table-small';
    const tbody = document.createElement('tbody');
    function createRow(label, value) {
      const tr = document.createElement('tr');
      const tdLabel = document.createElement('td');
      tdLabel.textContent = label;
      const tdValue = document.createElement('td');
      tdValue.textContent = value;
      tr.appendChild(tdLabel);
      tr.appendChild(tdValue);
      return tr;
    }
    tbody.appendChild(createRow('日期', `\${appointment.schedule_date} (\${appointment.start_time.replace(/:(\d{2})\$/, '')} ~ \${appointment.end_time.replace(/:(\d{2})\$/, '')})`));
    tbody.appendChild(createRow('科室', appointment.department));
    tbody.appendChild(createRow('类型', appointment.title));
    tbody.appendChild(createRow('院区', appointment.campus));
    tbody.appendChild(createRow('医生', appointment.doctor));
    tbody.appendChild(createRow('患者', appointment.patient));
    tbody.appendChild(createRow('联系电话', appointment.patient_phone));
    table.appendChild(tbody);
    li.appendChild(table);
    return li;
  };
  const refreshAppointments = async () => {
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/appointments', {
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
      const appointments = await response.json();
      const availableList = document.getElementById('history-list-available');
      if (availableList === null) {
        return;
      }
      const unavailableList = document.getElementById('history-list-unavailable');
      if (unavailableList === null) {
        return;
      }
      while (availableList.firstChild) {
        availableList.removeChild(availableList.firstChild);
      }
      while (unavailableList.firstChild) {
        unavailableList.removeChild(unavailableList.firstChild);
      }
      appointments.forEach(appointment => {
        if (appointment.available) {
          availableList.appendChild(drawAppointment(appointment));
        }
        else {
          unavailableList.appendChild(drawAppointment(appointment));
        }
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
  refreshAppointments();
};
main();
</script>