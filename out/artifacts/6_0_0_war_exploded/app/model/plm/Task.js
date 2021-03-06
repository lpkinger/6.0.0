Ext.define('erp.model.plm.Task', {
	extend : 'Gnt.model.Task',
	clsField : 'TaskType',
	baselineStartDateField : 'BaselineStartDate',
    baselineEndDateField : 'BaselineEndDate',
    baselinePercentDoneField : 'BaselinePercentdone',
	fields : [{ name : 'TaskType', type : 'string' },
	          { name : 'TaskColor', type : 'string'},
	          {name:'prjplanid',type:'int'},
	          {name:'prjplanname',type:'string'},
	          {name:'recorder',type:'string'},
	          {name:'recorddate',type:'string'},
	          {name:'taskcode',type:'string'},
	          {name:'id',type:'int'},
	          {name:'type',type:'int'},
	          {name:'resourcename',type:'string'},
	          {name:'handstatus',type:'string'},
	          {name:'realstartdate',type:'date',format:'Y-m-d'},
	          {name:'realenddate',type:'date',format:'Y-m-d'},
	          {name:'phasename',type:'string'},
	          {name:'phaseid',type:'int'},
	          {name:'prjdocname',type:'string'},
	          {name:'prjdocid',type:'string'},
	          {name:'prjdocstatus',type:'string'},
	          {name:'preconditioncode',type:'string'},
	          {name:'preconditionname',type:'string'},
	          {name:'backconditioncode',type:'string'},
	          {name:'backconditionname',type:'string'},
	          {name:'tasktype',type:'string'},
	           {name:'BaselinePercentdone',type:'int'},
	          {name:'BaselineStartDate',type:'date',format:'Y-m-d'},
	          {name:'BaselineEndDate',type:'date',format:'Y-m-d'}]
   });
