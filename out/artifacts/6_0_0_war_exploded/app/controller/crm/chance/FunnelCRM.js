Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.FunnelCRM', {
    extend: 'Ext.app.Controller',
    views:[
     		'crm.chance.FunnelCRM','core.trigger.DbfindTrigger','core.form.FtField',
     		'core.form.FtFindField','core.form.ConDateField'
     	],
    init:function(){
    	var me=this;
    	this.control({
    		'window':{
    			close:function(){
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			}
    		},
    		'window button[id=closebtn]':{
    			click:function(btn){
    				var main = parent.Ext.getCmp("content-panel"); 
    	    		main.getActiveTab().close();
    			},
    			afterrender:function(btn){
    				var sum=0;
    	    		var funneldata=new Array();
    	    		Ext.Ajax.request({
    	    			url:basePath+'crm/funnel.action',
    	    			params:'',
    	    			method : 'post',
    	    			async:false,
    	    			callback : function(options,success,response){
    	    				var res = new Ext.decode(response.responseText);
    	    				if(res.exceptionInfo != null){
    	            			showError(res.exceptionInfo);return;
    	            		}
    	    				if(res.data){
    	    					var i=0;
    	    					var data=new Ext.decode(res.data);
    	    					Ext.each(Ext.Object.getKeys(data),function(key){
    	    						sum+=data[key];
    	    						var d=new Array();
    	    						d[0]=key,d[1]=data[key];
    	    						funneldata[i]=d;
    	    						i++;
    	    					});
    	    				}
    	    			}
    	    		});
    	    		new Highcharts.Chart({
    					 chart: {
    						 reflow: false,
    						 renderTo: 'funnelwin-body',
    				            type: 'funnel',
    				            marginRight: 100,
    				            width:740,
    				            heigth:298
    				        },
    				        title: {
    				            text: '商机销售漏斗',
    				            x: -50
    				        },
    				        plotOptions: {
    				            series: {
    				                dataLabels: {
    				                    enabled: true,
    				                    formatter:function(){
    				                    	return '<b>'+this.key+'</b>  ('+this.y +')<br>--'+Math.round(this.y/sum*100)+'%';
    				                    },
    				                    color: 'black',
    				                    softConnector: true
    				                },
    				                neckWidth: '30%',
    				                neckHeight: '25%',
    				               
    				            }
    				        },
    				        legend: {
    				            enabled: false
    				        },
    				        series: [{
    				            name: '数  量',
    				            data:funneldata
    				        }]
    				});
    			}
    		},
    		'erpPrintFormPanel': {
    			titlechange: function(f){
    				if(f.title != null){
    					f.ownerCt.setTitle(f.title);
    					f.dockedItems.items[0].hide();
    				}
    			}
    		}
    	});
    }
});