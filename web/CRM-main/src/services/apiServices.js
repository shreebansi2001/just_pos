import { da } from "@faker-js/faker";
import { POST, GET, PUT, DELETE, UPLOAD } from "./axiosInstance";
import axios from "./axiosInstance";

//Country APIs
export const fetchCountries = (countryName = "") =>
  GET(`/countrymaster/getall?countryName=${countryName}`);

export const fetchCountryById = (id) => GET(`/countrymaster/getbyid?id=${id}`);

// State APIs
export const fetchStatesByCountry = (countryId, stateName = "") =>
  GET(
    `/statemaster/getbycountryid?countryId=${countryId}&stateName=${stateName}`,
  );

export const fetchStateById = (id) => GET(`/statemaster/getbyid?id=${id}`);

//City APIs
export const fetchCitiesByState = (stateId, cityName = "") =>
  GET(`/citymaster/getbystateid?stateId=${stateId}&cityName=${cityName}`);

//Login api
export const LoginUser = (data) => {
  return POST("/auth/login", data);
};

export const LoginOutUser = (email, eventtype) => {
  return GET(
    `/user-logs/logout-notification?email=${email}&eventType=${eventtype}`,
  );
};

export const getUserById = (id) => {
  return GET(`/user/getbyid?id=${id}`);
};

export const LoginWithOtp = async (phone) => {
  return axios.post("/auth/loginwithotp", null, {
    params: { mobileNo: phone },
  });
};

export const verifyMobileOtp = async ({ phone, otp, uniqueCode }) => {
  return axios.post(`/auth/verifyotpformobile`, null, {
    params: { mobileNo: phone, otp, uniqueCode },
  });
};

export const AddLead = (data) => {
  return POST(`/leadmaster/add`, data);
};

export const updatelead = (id , data )=>{
  return PUT(`/leadmaster/update?id=${id}` , data );
};

export const Addleadsource = (name  , userId ) => {
  return POST (`/lead-source/add?name=${name}&userId=${userId}`);
};

export const deletebyidsource = (leadSourceId) =>{
  return DELETE (`/lead-source/deletebyid?leadSourceId=${leadSourceId}`);
};

export const getallleadsource = (userId) =>{
  return GET(`/lead-source/getall?userId=${userId}`);
};

export const updateleadsource = (leadSourceId , name , userId) =>{
 return PUT(`/lead-source/update?leadSourceId=${leadSourceId}&name=${name}&userId=${userId}`);
};

export const Addleadstatus = (data) =>{
  return POST (`/lead-status/add`, data);
};


export const updatestatus = (leadStatusId, data) => {
  return PUT(`/lead-status/update?leadStatusId=${leadStatusId}`, data);  
};

export const DELETEstatus = (leadStatusId) => {
  return DELETE(`/lead-status/deletebyid?leadStatusId=${leadStatusId}`);  
};

export const getallstatus = (userId) =>{
return GET(`/lead-status/getall?userId=${userId}`);
};

export const fetchstatebycontry = (countryId, stateName = "") => {
  return GET (`/statemaster/getbycountryid?countryId=${countryId}&stateName=${stateName}`);
};

export  const fetchcitybystateid = (stateId, cityName = "") => {
  return GET (`/citymaster/getbystateid?stateId=${stateId}&cityName=${cityName}`);
};

export const FetchAllUser = (id) => {
  return GET(`/user/getallbyuserid?userId=${id}`);
};

export const GetAllRole = (id) => {
  return GET(`/rolemaster/getallbyuserid?userId=${id}`);
};

export const AddMember = (data) => {
  return POST(`/auth/add`, data);
};

export const UpdateMember = (id, data) => {
  return PUT(`/auth/update?id=${id}`, data);
};

export const AddRights = (data) => {
  return POST(`/user-rights/addRights`, data);
};



export const DeleteRole = (Id) => {
  return DELETE(`/rolemaster/deletebyid?id=${Id}`);
};
export const Addrole = (data) => {
  return POST(`/rolemaster/add`, data);
};


export const GetAllPages = (isadmin, iscombo) => {
  return GET(
    `/user-rights/getPages?isAdminRights=${isadmin}&isCombine=${iscombo}`,
  );
};

export const GetAllMemberByUserId = (id) => {
  return GET(`/user/getallbyuserid?userId=${id}`);
};
export const Addeventtype = (data) => {
  return POST(`/eventtype/add`, data);
};


export const EditEventType = (Id, data) => {
  return PUT(`/eventtype/update?id=${Id}`, data);
};
export const GetEventType = (Id) => {
  return GET(`/eventtype/getallbyuserid?userId=${Id}`);
};
export const DeleteEventType = (Id) => {
  return DELETE(`/eventtype/deletebyid?id=${Id}`);
};
export const SearchEventType = (data, Id) => {
  return GET(`/eventtype/getallbyuserid?eventTypeName=${data}&userId=${Id}`);
};

export const Translateapi = (data) => {
  return GET(`/transliterate?text=${data}`);
};

export const GetLeadCode = (userId ) => {
  return GET(`/leadmaster/generateLeadCode?userId=${userId}`);
};

export const GetAllLead = (userId  , AssignId) => {
  return GET(`/leadmaster/getAll?userId=${userId}&AssignId=${AssignId}`);
};
export const GetRightsBYroleId = (roleId) => {
  return GET(`/user-rights/getByRole?roleId=${roleId}`);
};

export const getleadcountbystatus = (userId) =>{
  return GET(`/leadmaster/getCountLeadByLeadStatus?userId=${userId}`);
};

export const deleteLeadById = ( id ) =>{
  return DELETE(`/leadmaster/deleteById?id=${id}`);
};

export const changeLeadStatus = (leadIds , leadStatusId) =>{
  return PUT(`/leadmaster/changeLeadStatus?leadIds=${leadIds}&leadStatusId=${leadStatusId}`);
};

export const assignMultipleLeadToMember = (closeDate  , description , leadId , memberId ) =>{
  return PUT(`/leadmaster/assignMultipleLeadToMember?closeDate=${closeDate}&description=${description}&leadId=${leadId}&memberId=${memberId}`);
};

export const getAllFollowUpByMemberId = (memberId , userId) =>{
  return GET(`/leadmaster/getAllFollowUpByMemberId?memberId=${memberId}&userId=${userId}`);
};

export const AddExclusiveReport = (formData) => {
  return POST("/report/menu-planning-exclusive/", formData);
};

export const GetReportConfiguration = (mappingId, moduleId) => {
  return GET(
    `/report/configuration/get?mappingId=${mappingId}&moduleId=${moduleId}`,
  );
};

export const GettemplatebyuserId = () => {
  return GET(`templatemodulemaster/getall`);
};

export const GetAllCustomThemeByUserIdAndModuleId = (Id, moduleId) => {
  return GET(
    `/admintemplatemodule/getall?templateModuleId=${moduleId}&userId=${Id}`,
  );
};

export const addupdatefollowup = (data) =>{
  return PUT(`/leadmaster/addorupdatefollowup`,data);
};

export const searchfliterlead = (leadAssignId , priority , sourceId  , statusId) =>{
  return GET (`/leadmaster/search?leadAssignId=${leadAssignId}&priority=${priority}&sourceId=${sourceId}&statusId=${statusId}`);
};

export const updateusermaster = (id, data) => {
  return PUT(`/auth/update?id=${id}`, data);
};

export const getAllByRoleId = (roleId, type) => {
  return GET(`/user/getallbyroleid?roleId=${roleId}&type=${type}`);
};

export const Fetchmanager = (Id) => {
  return GET(`/user/getmanagerandadminusersbyclient?clientUserId=${Id}`);
};

export const RefreshToken = () => {
  return POST(`/auth/refresh-token`);
};

export const getOrCreatePartyId = (leadId) => {
  return PUT(`partymaster/create-or-get?leadId=${leadId}`);
};