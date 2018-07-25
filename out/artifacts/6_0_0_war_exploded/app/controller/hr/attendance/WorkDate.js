Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.WorkDate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.WorkDate','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'timefield' : {
    			afterrender : function(t){
    				t.increment = 15;
    			},
    			/*change : function(t){
    				var wd_iscross = Ext.getCmp("wd_iscross").value;
    				if(wd_iscross==1){
    					t.getErrors = function(){}
    				}
    			},*/
    	 		focus : function(t){
    	 			var wd_iscross = Ext.getCmp("wd_iscross").value;
    				if(wd_iscross==1){
    					t.getErrors = function(){}
    				}
    			}
    		},
    		'erpSaveButton': {
    			click : function(btn){
    				var wd_iscross = Ext.getCmp("wd_iscross").value;
    				if(wd_iscross==1){
    					this.FormUtil.beforeSave(this);
    				}else{
    					var flag=1;
        				flag=me.check();
        				if(flag){
    	    				this.FormUtil.beforeSave(this);
        				}
    				}
    			}
    		},
    		'erpCloseButton': {
    			afterrender:function(btn){
    				var form = me.getForm(btn);
    				var degree=Ext.getCmp('wd_degree').value;
	    			if(degree==1||degree==0.5){
	    				Ext.Array.each(form.items.items,function(f){
		    				if(f.groupName=='正班二'){
		    					f.hide();
		    				}
	    				});
	    			}else if(degree==2){
	    				Ext.Array.each(form.items.items,function(f){
	    					if(f.groupName=='正班二'){
	    						f.show();
	    					}
	    				});
	    			}else if(degree==3){
	    				Ext.Array.each(form.items.items,function(f){
	    					if(f.groupName=='正班二'||f.groupName=='正班三'){
	    						f.show();
	    					}
		   				});
	    			}	    			
	      		
    			},
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var wd_iscross = Ext.getCmp("wd_iscross").value;
    				if(wd_iscross==1){
    					this.FormUtil.onUpdate(this);
    				}else{
    					var flag=1;
        				flag=me.check();
        				if(flag){
        					this.FormUtil.onUpdate(this);
    					}
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wd_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWorkDate', '新增考勤项目', 'jsps/hr/attendance/workdate.jsp');
    			}
    		},
    		'field[name=wd_degree]':{
	    		change:function(f){
	    			var form =Ext.getCmp('form');
	    			if(f.value==1||f.value==0.5){
	    				Ext.Array.each(form.items.items,function(f){
		    				if(f.groupName=='正班二'||f.groupName=='正班三'){
		    					f.hide();
		    				}
	    				});
	    			}else if(f.value==2){
	    				Ext.Array.each(form.items.items,function(f){
	    					if(f.groupName=='正班二'){
	    						f.show();
	    					}
	    				});
	    			}else if(f.value==3){
	    				Ext.Array.each(form.items.items,function(f){
	    					if(f.groupName=='正班二'||f.groupName=='正班三'){
	    						f.show();
	    					}
		   				});
	    			}	    			
	      		}
    		},
    		//正班一
    		'field[name=wd_ondutyone]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('wd_onend1').setValue(t);
	        		Ext.getCmp('wd_onbeg1').setMaxValue(t);
	        		Ext.getCmp('wd_offdutyone').setMinValue(t);
	        		if(t&&Ext.getCmp('wd_onbeg1').getValue()){
    					var a=Ext.getCmp('wd_onend1').getValue().getHours()*60+Ext.getCmp('wd_onend1').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg1').getValue().getHours()*60+Ext.getCmp('wd_onbeg1').getValue().getMinutes();
    					Ext.getCmp('wd_before1').setValue(a-b);//计提前
    				}else{
    					Ext.getCmp('wd_before1').setValue(0);
    				}
	        	 }
    		},
    		'field[name=wd_onbeg1]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_onend1').getValue()&&c.value){
    					var a=Ext.getCmp('wd_onend1').getValue().getHours()*60+Ext.getCmp('wd_onend1').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg1').getValue().getHours()*60+Ext.getCmp('wd_onbeg1').getValue().getMinutes();
    					Ext.getCmp('wd_before1').setValue(a-b);//计提前
    				}else{
    					Ext.getCmp('wd_before1').setValue(0);
    				}
	        	 }
    		},
    		'field[name=wd_offdutyone]':{
    			change : function(c) {
	        		 var t = c.getValue();
	        		 Ext.getCmp('wd_offbeg1').setValue(t);
	        		 Ext.getCmp('wd_offend1').setMinValue(t);
	        		 Ext.getCmp('wd_ondutyone').setMaxValue(t);
        			if(t&&Ext.getCmp('wd_offend1').getValue()){
	   					var a=Ext.getCmp('wd_offend1').getValue().getHours()*60+Ext.getCmp('wd_offend1').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg1').getValue().getHours()*60+Ext.getCmp('wd_offbeg1').getValue().getMinutes();
    					Ext.getCmp('wd_last1').setValue(a-b);//计提前
    				}else{
    					Ext.getCmp('wd_last1').setValue(0);
    				}
    			}
    		},
    		'field[name=wd_offend1]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_offbeg1').getValue()&&c.value){
    					var a=Ext.getCmp('wd_offend1').getValue().getHours()*60+Ext.getCmp('wd_offend1').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg1').getValue().getHours()*60+Ext.getCmp('wd_offbeg1').getValue().getMinutes();
    					Ext.getCmp('wd_last1').setValue(a-b);
    				}else{
    					Ext.getCmp('wd_last1').setValue(0);
    				}
	        	 }
    		},
   			'field[name=wd_overw1]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overh1').setValue(false);
    				}
	        	 }
    		},
    		'field[name=wd_overh1]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overw1').setValue(false);
    				}
	        	 }
    		},//正班二
    		'field[name=wd_ondutytwo]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('wd_onend2').setValue(t);
	        		Ext.getCmp('wd_onbeg2').setMaxValue(t);
	        		Ext.getCmp('wd_offdutytwo').setMinValue(t);
        			if(t&&Ext.getCmp('wd_onbeg2').getValue()){
    					var a=Ext.getCmp('wd_onend2').getValue().getHours()*60+Ext.getCmp('wd_onend2').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg2').getValue().getHours()*60+Ext.getCmp('wd_onbeg2').getValue().getMinutes();
    					Ext.getCmp('wd_before2').setValue(a-b);//计提前
   					}else{
   						Ext.getCmp('wd_before2').setValue(0);
   					}
   				}
    		},
    		'field[name=wd_onbeg2]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_onend2').getValue()&&c.value){
    					var a=Ext.getCmp('wd_onend2').getValue().getHours()*60+Ext.getCmp('wd_onend2').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg2').getValue().getHours()*60+Ext.getCmp('wd_onbeg2').getValue().getMinutes();
    					Ext.getCmp('wd_before2').setValue(a-b);//计提前
    				}else{
    					Ext.getCmp('wd_before2').setValue(0);//计提前
    				}
	        	 }
    		},
    		'field[name=wd_offdutytwo]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('wd_offbeg2').setValue(t);
	        		Ext.getCmp('wd_offend2').setMinValue(t);
	        		Ext.getCmp('wd_ondutytwo').setMaxValue(t);
					if(t&&Ext.getCmp('wd_offend2').getValue()){
	    				var a=Ext.getCmp('wd_offend2').getValue().getHours()*60+Ext.getCmp('wd_offend2').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg2').getValue().getHours()*60+Ext.getCmp('wd_offbeg2').getValue().getMinutes();
    					Ext.getCmp('wd_last2').setValue(a-b);
    				}else{
    					Ext.getCmp('wd_last2').setValue(0);
    				}
   	        	}
    		},
    		'field[name=wd_offend2]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_offbeg2').getValue()&&c.value){
    					var a=Ext.getCmp('wd_offend2').getValue().getHours()*60+Ext.getCmp('wd_offend2').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg2').getValue().getHours()*60+Ext.getCmp('wd_offbeg2').getValue().getMinutes();
    					Ext.getCmp('wd_last2').setValue(a-b);
    				}else{
    					Ext.getCmp('wd_last2').setValue(0);
    				}
	        	 }
    		},
   			'field[name=wd_overw2]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overh2').setValue(false);
    				}
	        	 }
    		},
    		'field[name=wd_overh2]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overw2').setValue(false);
    				}
	        	 }
    		},//正班三
    		'field[name=wd_ondutythree]':{
    			change : function(c) {
	        		var t = c.getValue();
	        		Ext.getCmp('wd_onend3').setValue(t);
	        		Ext.getCmp('wd_onbeg3').setMaxValue(t);
	        		Ext.getCmp('wd_offdutythree').setMinValue(t);
        			if(t&&Ext.getCmp('wd_onbeg3').getValue()){
    					var a=Ext.getCmp('wd_onend3').getValue().getHours()*60+Ext.getCmp('wd_onend3').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg3').getValue().getHours()*60+Ext.getCmp('wd_onbeg3').getValue().getMinutes();
    					Ext.getCmp('wd_before3').setValue(a-b);//计提前
   					}else{
   						Ext.getCmp('wd_before3').setValue(0);//计提前
   					}
	        	 }
    		},
    		'field[name=wd_onbeg3]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_onend3').getValue()&&c.value){
    					var a=Ext.getCmp('wd_onend3').getValue().getHours()*60+Ext.getCmp('wd_onend3').getValue().getMinutes();
    					var b=Ext.getCmp('wd_onbeg3').getValue().getHours()*60+Ext.getCmp('wd_onbeg3').getValue().getMinutes();
    					Ext.getCmp('wd_before3').setValue(a-b);//计提前
    				}else{
    					Ext.getCmp('wd_before3').setValue(0);//计提前
    				}
	        	 }
    		},
    		'field[name=wd_offdutythree]':{
    			change : function(c) {
	        		 var t = c.getValue();
	        		 Ext.getCmp('wd_offbeg3').setValue(t);
	        		 Ext.getCmp('wd_offend3').setMinValue(t);
	        		 Ext.getCmp('wd_ondutythree').setMaxValue(t);
        			if(t&&Ext.getCmp('wd_offend3').getValue()){
    					var a=Ext.getCmp('wd_offend3').getValue().getHours()*60+Ext.getCmp('wd_offend3').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg3').getValue().getHours()*60+Ext.getCmp('wd_offbeg3').getValue().getMinutes();
    					Ext.getCmp('wd_last3').setValue(a-b);
   					}else{
   						Ext.getCmp('wd_last3').setValue(0);//计提前
   					}
	        	 }
    		},
    		'field[name=wd_offend3]':{
    			change : function(c) {
    				if(Ext.getCmp('wd_offbeg3').getValue()&&c.value){
    					var a=Ext.getCmp('wd_offend3').getValue().getHours()*60+Ext.getCmp('wd_offend3').getValue().getMinutes();
    					var b=Ext.getCmp('wd_offbeg3').getValue().getHours()*60+Ext.getCmp('wd_offbeg3').getValue().getMinutes();
    					Ext.getCmp('wd_last3').setValue(a-b);
    				}else{
    					Ext.getCmp('wd_last3').setValue(0);
    				}
	        	 }
    		},
   			'field[name=wd_overw3]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overh3').setValue(false);
    				}
	        	 }
    		},
    		'field[name=wd_overh3]':{
    			change : function(c) {
    				if(c.getValue()){
    					Ext.getCmp('wd_overw3').setValue(false);
    				}
	        	 }
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	check:function(){
		var hours=Ext.getCmp('wd_hours').value-0;
    	if(hours>24){
    		showError('班次时数不能大于24');
    		return 0;
    	}
    	var wd_degree=Ext.getCmp('wd_degree').value;//班段数量 
    	if(wd_degree==1||wd_degree==0.5){
    		return this.checkW1();
    	}else if(wd_degree==2){
    		return this.checkW2();
    	}else if(wd_degree==3){
	    	return this.checkW3();
    	}
	},
	checkW1:function(){
		//正班一
    	var w1_on=Ext.getCmp('wd_ondutyone').value;
    	var w1_off=Ext.getCmp('wd_offdutyone').value;
    	var w1_ondk=Ext.getCmp('wd_onbeg1').value;
    	var w1_offdk=Ext.getCmp('wd_offend1').value;
    	if(!(w1_on&&w1_off&&w1_ondk&&w1_offdk)){
    		showError('正班一时间设置不完整');
    		return 0;
    	};
    	return 1;
	},
	checkW2:function(){
		var f1=this.checkW1();
		//正班二
		if(f1){
			var w2_on=Ext.getCmp('wd_ondutytwo').value;
	    	var w2_off=Ext.getCmp('wd_offdutytwo').value;
	    	var w2_ondk=Ext.getCmp('wd_onbeg2').value;
	    	var w2_offdk=Ext.getCmp('wd_offend2').value;
	    	if(!(w2_on&&w2_off&&w2_ondk&&w2_offdk)){
	    		showError('正班二时间设置不完整');
	    		return 0;
	    	}else{
	    		if(Ext.getCmp('wd_offend1').value>=w2_ondk){
		    		showError('正班二上班打卡起始时间要晚于正班一下班打卡结束时间');
		    		return 0;
	    		}
	    	};
	   		return 1;
	   	}
    	return 0;
    },
    checkW3:function(){
    	var f1=this.checkW2();
    	if(f1){
	    	//正班三
	    	var w3_on=Ext.getCmp('wd_ondutythree').value;
	    	var w3_off=Ext.getCmp('wd_offdutythree').value;
	    	var w3_ondk=Ext.getCmp('wd_onbeg3').value;
	    	var w3_offdk=Ext.getCmp('wd_offend3').value;
	    	if(!(w3_on&&w3_off&&w3_ondk&&w3_offdk)){
	    		showError('正班三时间设置不完整');
	    		return 0;
	    	}else{
	    		if(Ext.getCmp('wd_offend2').value>=w3_ondk){
		    		showError('正班三上班打卡起始时间要晚于正班二下班打卡结束时间');
		    		return 0;
	    		}
	    	};
	    	return 1;
    	}
		return 0;
    }
});