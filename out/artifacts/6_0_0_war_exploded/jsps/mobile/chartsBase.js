//判断数组中是否包含某个元素
Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
};
 
var HighChart = {
    ChartDataFormate: {   	
        FormateNOGroupData: function (data) {
            var categories = [];
            var datas = [];
            for (var i = 0; i < data.length; i++) {
                categories.push(data[i].xField || "");
                datas.push([data[i].xField, data[i].yField || 0]);
            }
            return { category: categories, data: datas };
        },
        FormatGroupData: function (data,display) {//处理分组数据，数据格式：xField：XXX，gField：XXX，yField：XXX
            var names = new Array();
            var groups = new Array();
            var series = new Array();
            if(data.length>0 && data[0].gField){
            	  for (var i = 0; i < data.length; i++) {
                      if (!names.contains(data[i].xField))
                          names.push(data[i].xField);
                      if (!groups.contains(data[i].gField))
                          groups.push(data[i].gField);
                  }
                  for (var i = 0; i < groups.length; i++) {
                      var temp_series = {};
                      var temp_data = new Array();
                      for (var j = 0; j < data.length; j++) {
                          for (var k = 0; k < names.length; k++)
                              if (groups[i] == data[j].gField && data[j].xField == names[k])
                                  temp_data.push(Number(data[j].yField));
                      }
                      temp_series = { name: groups[i], data: temp_data };
                      series.push(temp_series);
                  }
            }else {
            	  for (var i = 0; i < data.length; i++) {
            		  var xField = data[i].xField;
            		  if(typeof(xField)=='object'){	//数据格式是否为新柱状图,如：[{"xField":{"EM_DEFAULTORNAME":"软件科"},"yField":{"VAL":"2","TESTS":"8"}}]
            			  names.push(Object.values(xField)[0]);
            		  }else{
            			  names.push(xField);
            		  }
                  }
            	  var temp_data = new Array();

            	  var y = data[0].yField;
            	  var keyArray;
            	  if(typeof(y)=='object'){	//数据格式是否为新柱状图,如：[{"xField":{"EM_DEFAULTORNAME":"软件科"},"yField":{"VAL":"2","TESTS":"8"}}]
            		  keyArray = Object.keys(y);
            		  if(display != null && display != ''){
            			  var yNames = display.split(',');
            		  }
            		  for(var i = 0; i < keyArray.length; i++){
            			  var arr = new Array();
            			  for(var j = 0; j < data.length; j++){
            				  //var temp = data[j].yField[keyArray[i]];
            				  arr.push(Number(data[j].yField[keyArray[i]]));
                    	  }
            			  if(yNames){
            				  series.push({name: yNames[i],data: arr,showInLegend: true});
            			  }else{
            				  series.push({name: display,data: arr,showInLegend: true});
            			  }
            			  
            		  }
            	  }else{
            		  var temp_data = new Array();
            		  for(var k = 0; k < names.length; k++){
            			  for (var m = 0; m < data.length; m++) {                       
                              if (data[m].xField == names[k])
                                  temp_data.push(Number(data[m].yField));
            			  } 
            		  }
            		  series.push({name: display,data: temp_data,showInLegend: true});
            	  }
                  //series.push({name: display,data: temp_data});
            }
          
            return { category: names, series: series };
        }
    },
    ChartOptionTemplates: {
    	MobilePie: function (data, name, title) {
            var pie_datas = HighChart.ChartDataFormate.FormateNOGroupData(data);
            var option = {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: title || '',
                    style:{
                    	fontSize: '15px'
                    }
                },
                tooltip: {
                    pointFormat:'<span style="color:{point.color}">\u25CF</span><b>{point.realpercentage:.2f}%</b><br/><span style="color:{point.color}">\u25CF</span><b>{point.y}</b> {series.name}'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: false
                         },
                        showInLegend: true
                    }
                },
                legend: {
                    backgroundColor: '#FFFFFF',
                    labelFormatter: function () {
                        return this.name +':'+this.realpercentage.toFixed(2)+'%';
                    }
                },
                series: [{
                    type: 'pie',
                    name: name || '',
                    data: pie_datas.data
                }]
            };
            return option;
        },
        Pie: function (data, name, title) {
            var pie_datas = HighChart.ChartDataFormate.FormateNOGroupData(data);
            var option = {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: title || '',
                    style:{
                    	fontSize: '15px'
                    }
                },
                tooltip: {
                    pointFormat:'<span style="color:{point.color}">\u25CF</span><b>{point.y}</b> {series.name}'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                          /*  formatter : function() {  
                            	if (this.point.percentage.toFixed(2)>0)
                              return "<p style='width: 100px; display:inline-block; white-space:pre-wrap;'><b>"+this.point.name+"</b>: "+this.percentage.toFixed(2)+" %</p>";
                                },*/
                            format: '<b>{point.name}</b>: {point.realpercentage:.2f} %',
                            style: {
                                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black',
                            }
                         },
                        showInLegend: true
                    }
                },
                series: [{
                    type: 'pie',
                    name: name || '',
                    data: pie_datas.data
                }]
            };
            return option;
        },
        Line: function (data, name, title,display) {
            var line_datas = HighChart.ChartDataFormate.FormatGroupData(data,display);
            var option = {
            	chart: {
                    type: 'line'
                },
                title: {
                    text: title || '',
                    style:{
                    	fontSize: '15px'
                    },
                    x: -20
                },             
                xAxis: {
                    categories: line_datas.category,
                    lineColor:'#C0D0E0',
                    lineWidth:1
                },
                yAxis: {
                    title: {
                        text: name,
                        align: 'high',                      
                    },
                    lineColor:'#C0D0E0',
                    lineWidth:1,
                    allowDecimals:false //是否允许刻度有小数
                }, 
                plotOptions: {
                	line:{dataLabels: {
                          enabled: true,
                          }},
                    series : {
                    	events : {
                    	legendItemClick: function(event) 
                    	{  return false;
                    	}
                    }
                   }
                },
                series: line_datas.series
            };
            return option;
        },
        Column: function (data, name, title,display,type) {
            var column_datas = HighChart.ChartDataFormate.FormatGroupData(data,display);
            var option = {
                chart: {
                    type: type
                },
                title: {
                    text: title || '',
                    style:{
                    	fontSize: '15px'
                    }
                },
                subtitle: {
                    text: ''
                },
                credits: {
                    enabled: false
                },
                xAxis: {
                    categories: column_datas.category,
                    lineColor:'#C0D0E0',
                    lineWidth:1
                },
                yAxis: {
                    title: {
                        text: name,
                        align: 'high',                      
                    },
                    lineColor:'#C0D0E0',
                    lineWidth:1,
                    allowDecimals:false //是否允许刻度有小数
                },
                tooltip: {
                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}:</td>' +
                        '<td style="padding:0"><b>{point.y:.2f}</b></td></tr>',
                   // pointFormat:'<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>',
                    footerFormat: '</table>',
                    shared: true,
                    useHTML: true
                },
                plotOptions: {
                    column: {
                    	groupPadding:0.3,
                        pointPadding: 0.2,
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                        }
                    },
                    bar: {
                    	groupPadding:0.3,
                        pointPadding: 0.2,
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            }
                    },
                    series : {
                    	events : {
                    	legendItemClick: function(event) 
                    	{  
                    		return false;
                    	}
                    }
                   }
                },
                legend: {
                    align: 'right',
                    verticalAlign: 'top',
                    x: 0,
                    y: 30
                },
                series: column_datas.series
            };
            return option;
        }
    },
    RenderChart: function (charts) {
    	Highcharts.setOptions({ 
            lang: { 
            	numericSymbols: [ ",000" , ",000,000" , ",000,000,000" , "x10^12" , "x10^15" , "x10^18"]
            } 
        });
    	  for (var i = 0; i < charts.length; i++) {
    		  $("#"+charts[i].container).highcharts(charts[i].opt);
          }        
    }
};