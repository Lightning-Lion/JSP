<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<form class="appointment">
  <h1 class="appointment-title">预约挂号</h1>
  <h2>挂号选择</h2>
  <div class="appointment-filter" id="appointment-filter" style="display: none;">
    <div class="appointment-filter-item">
      <span>日期</span>
      <div class="dropdown appointment-filter-date" name="schedule_date">
        <div class="dropdown-trigger" tabindex="0"><span></span><jsp:include page="/components/icon/IconDown.jsp" /></div>
        <ul class="dropdown-list" style="display: none;"></ul>
      </div>
    </div>
    <div class="appointment-filter-item">
      <span>科别</span>
      <div class="dropdown appointment-filter-division" name="division">
        <div class="dropdown-trigger" tabindex="0"><span></span><jsp:include page="/components/icon/IconDown.jsp" /></div>
        <ul class="dropdown-list" style="display: none;"></ul>
      </div>
    </div>
    <div class="appointment-filter-item">
      <span>科室</span>
      <div class="dropdown appointment-filter-department" name="department">
        <div class="dropdown-trigger" tabindex="0"><span></span><jsp:include page="/components/icon/IconDown.jsp" /></div>
        <ul class="dropdown-list" style="display: none;"></ul>
      </div>
    </div>
    <div class="appointment-filter-item">
      <span>类型</span>
      <div class="dropdown appointment-filter-type" name="type">
        <div class="dropdown-trigger" tabindex="0"><span></span><jsp:include page="/components/icon/IconDown.jsp" /></div>
        <ul class="dropdown-list" style="display: none;"></ul>
      </div>
    </div>
    <div class="appointment-filter-item">
      <span>院区</span>
      <div class="dropdown appointment-filter-campus" name="campus">
        <div class="dropdown-trigger" tabindex="0"><span></span><jsp:include page="/components/icon/IconDown.jsp" /></div>
        <ul class="dropdown-list" style="display: none;"></ul>
      </div>
    </div>
    <div class="appointment-filter-item">
      <span>医生</span>
      <input class="input appointment-filter-doctor" type="text" name="doctor">
    </div>
  </div>
  <button class="button appointment-list-refresh" id="appointment-list-refresh" type="button" style="display: none;">查询</button>
  <div class="appointment-list">
    <table class="table appointment-list-table">
      <thead>
        <tr>
          <th>日期</th>
          <th>科室</th>
          <th>院区</th>
          <th>医生</th>
          <th>类型</th>
          <th>价格</th>
          <th>剩余</th>
          <th></th>
        </tr>
      </thead>
      <tbody id="appointment-schedule"></tbody>
    </table>
  </div>
  <h2>预约信息</h2>
  <div class="appointment-profile-controller">
    <span>可在“信息管理”中管理预约信息</span>
    <button class="button-icon" id="appointment-profile-refresh" title="刷新预约信息" type="button"><jsp:include page="/components/icon/IconRefresh.jsp" /></button>
  </div>
  <div class="appointment-profile" id="appointment-profile"></div>
  <button class="button appointment-submit" id="appointment-submit" type="submit" title="付款提交" disabled>付款提交</button>
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
  const filtersData = {
    schedule_date: [],
    departments: {},
    campus: ['不限'],
    type: ['不限']
  };
  const filtersElement = document.getElementById('appointment-filter');
  const filtersRefreshButton = document.getElementById('appointment-list-refresh');
  if (filtersElement === null || filtersRefreshButton === null) {
    return;
  }
  const filters = {
    schedule_date: '',
    division: '',
    department: '',
    campus: '',
    doctor: '',
    type: ''
  };
  const drawScheduleRow = (schedule) => {
    const tr = document.createElement('tr');
    const rowData = [
      `\${schedule.schedule_date} (\${schedule.start_time.replace(/:(\d{2})\$/, '')} ~ \${schedule.end_time.replace(/:(\d{2})\$/, '')})`,
      schedule.department,
      schedule.campus,
      schedule.doctor,
      schedule.type,
      schedule.price,
      schedule.remaining
    ];
    rowData.forEach(data => {
      const td = document.createElement('td');
      td.textContent = data;
      tr.appendChild(td);
    });
    const tdButton = document.createElement('td');
    const button = document.createElement('button');
    button.className = 'button appointment-list-select';
    button.type = 'button';
    button.title = '预约';
    button.textContent = '预约';
    button.setAttribute('data-id', schedule.id);
    button.addEventListener('click', () => {
      if (scheduleId !== null) {
        const lastButton = document.querySelector(`#appointment-list button[data-id="\${scheduleId}"]`);
        if (lastButton !== null) {
          lastButton.disabled = false;
        }
      }
      button.disabled = true;
      scheduleId = schedule.id;
      checkAppointment();
    });
    tdButton.appendChild(button);
    tr.appendChild(tdButton);
    return tr;
  };
  const doctorInput = filtersElement.querySelector('.appointment-filter-doctor');
  if (doctorInput === null) {
    return;
  }
  const refreshSchedules = async () => {
    filters.doctor = doctorInput.value || '';
    const fetchBody = {
      schedule_date: filters.schedule_date,
      department: filters.department
    };
    if (filters.campus !== '不限') {
      fetchBody.campus = filters.campus;
    }
    if (filters.type !== '不限') {
      fetchBody.type = filters.type;
    }
    if (filters.doctor !== '') {
      fetchBody.doctor = filters.doctor;
    }
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/schedules', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(fetchBody)
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
      scheduleId = null;
      checkAppointment();
      const schedules = await response.json();
      const list = document.getElementById('appointment-schedule');
      if (list === null) {
        return;
      }
      while (list.firstChild) {
        list.removeChild(list.firstChild);
      }
      schedules.forEach(schedule => {
        list.appendChild(drawScheduleRow(schedule));
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
  const resetDropdown = (key, value) => {
    const createDropdownList = (value) => {
      const element = document.createElement('li');
      element.classList.add('dropdown-item');
      if (value === filters[key]) {
        element.classList.add('dropdown-item-active');
      }
      const item = document.createElement('a');
      item.href = '#';
      item.textContent = value;
      item.addEventListener('click', (event) => {
        event.preventDefault();
        resetDropdown(key, value);
      });
      element.appendChild(item);
      return element;
    };
    const dropdown = document.querySelector(`.dropdown[name="\${key}"]`);
    if (dropdown === null) {
      return;
    }
    const dropdownList = dropdown.querySelector('.dropdown-list');
    if (dropdownList === null) {
      return;
    }
    if (value === undefined) {
      while (dropdownList.firstChild) {
        dropdownList.removeChild(dropdownList.firstChild);
      }
      let data = [];
      if (key === 'division') {
        data = Object.keys(filtersData.departments);
      }
      else if (key === 'department') {
        if (filters.division in filtersData.departments) {
          data = filtersData.departments[filters.division];
        }
      }
      else if (key in filtersData && key !== 'departments') {
        data = filtersData[key];
      }
      else {
        return;
      }
      if (data.length === 0) {
        return;
      }
      filters[key] = data[0];
      data.forEach(item => {
        dropdownList.appendChild(createDropdownList(item));
      });
    }
    else {
      filters[key] = value;
      const dropdownItems = dropdownList.querySelectorAll('a');
      dropdownItems.forEach(item => {
        if (item.textContent === value) {
          item.parentElement?.classList.add('dropdown-item-active');
        }
        else {
          item.parentElement?.classList.remove('dropdown-item-active');
        }
      });
    }
    const dropdownTrigger = dropdown.querySelector('.dropdown-trigger span');
    if (dropdownTrigger === null) {
      return;
    }
    dropdownTrigger.textContent = filters[key];
    if (key === 'division') {
      resetDropdown('department');
    }
  };
  const dropdownElements = document.querySelectorAll('.dropdown');
  document.addEventListener('click', (event) => {
    const target = Array.from(dropdownElements).find((dropdown) => dropdown.contains(event.target));
    dropdownElements.forEach((dropdown) => {
      const dropdownList = dropdown.querySelector('.dropdown-list');
      if (dropdownList === null) {
        return;
      }
      if (dropdown === target) {
        if (event.target.closest('.dropdown-list')) {
          if (event.target.tagName === 'A') {
            dropdownList.style.display = 'none';
          }
        }
        else {
          dropdownList.style.display = dropdownList.style.display === '' ? 'none' : '';
        }
      }
      else {
        dropdownList.style.display = 'none';
      }
    });
  });
  let scheduleId = null;
  let profileId = null;
  const submit = document.getElementById('appointment-submit');
  submit?.addEventListener('click', async (event) => {
    event.preventDefault();
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/appointments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          schedule_id: scheduleId,
          profile_id: profileId
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
        window.location.href = '/history';
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
  const checkAppointment = () => {
    if (submit !== null) {
      submit.disabled = !(scheduleId !== null && profileId !== null);
    }
  };
  const drawProfileButton = (profile) => {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'appointment-profile-select';
    button.title = '选择此身份';
    button.setAttribute('data-id', profile.id);
    const table = document.createElement('table');
    table.className = 'table-small appointment-profile-table';
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
    button.appendChild(table);
    button.addEventListener('click', () => {
      if (profileId !== null) {
        const lastButton = document.querySelector(`#appointment-profile button[data-id="\${profileId}"]`);
        if (lastButton !== null) {
          lastButton.disabled = false;
        }
      }
      button.disabled = true;
      profileId = profile.id;
      checkAppointment();
    });
    return button;
  };
  const refreshProfiles = async () => {
    profileId = null;
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
      const list = document.getElementById('appointment-profile');
      if (list === null) {
        return;
      }
      while (list.firstChild) {
        list.removeChild(list.firstChild);
      }
      profiles.forEach(profile => {
        list.appendChild(drawProfileButton(profile));
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
    checkAppointment();
  };
  filtersRefreshButton.addEventListener('click', () => {
    refreshSchedules();
  });
  document.getElementById('appointment-profile-refresh')?.addEventListener('click', () => {
    refreshProfiles();
  });
  const getFiltersData = async () => {
    const loading = loadingDialog();
    let response = null;
    try {
      response = await fetch('/api/filters', {
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
      const filters = await response.json();
      filtersData.schedule_date = filtersData.schedule_date.concat(filters.schedule_date);
      for (const [key, value] of Object.entries(filters.departments)) {
        filtersData.departments[key] = (filtersData.departments[key] || []).concat(value);
      }
      filtersData.campus = filtersData.campus.concat(filters.campus);
      filtersData.type = filtersData.type.concat(filters.type);
      Object.keys(filtersData).forEach(key => {
        resetDropdown(key);
      });
      resetDropdown('division');
      filtersElement.style.display = '';
      filtersRefreshButton.style.display = '';
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
  getFiltersData();
  refreshProfiles();
};
main();
</script>