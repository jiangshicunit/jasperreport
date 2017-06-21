define('d3Circle', ['d3'], function (d3) {

  return function (instanceData) {

    var w = instanceData.width,
      h = instanceData.height;

    console.log(instanceData);

    var svg = d3.select("#" + instanceData.id).insert("svg")
      .attr("id", instanceData.id + "svg")
      .attr("width", w)
      .attr("height", h);

    var redLeft = 0, yellowLeft = 0, G = 0, yellowRight = 0, redRight = 0, testing = parseFloat(instanceData.testing),
      minNumber = instanceData.minNumber === null || !instanceData.minNumber ? null : parseFloat(instanceData.minNumber),
      maxNumber = instanceData.maxNumber === null || !instanceData.maxNumber ? null : parseFloat(instanceData.maxNumber),
      Proportion = 0.6, totalSize = w, V = 0, sum = 0, difference = maxNumber - minNumber;

    if (maxNumber && minNumber !== null) {
      Proportionality(6, 0.6, 1);
      sum = difference / Proportion;
      yellowRight = totalSize * Proportion * 0.027;
      yellowLeft = yellowRight;
      G = (totalSize * Proportion) - yellowLeft * 2;
      redLeft = (totalSize * (1 - Proportion)) / 2;
      redRight = redLeft;
      V = totalSize / sum * (testing - minNumber) + redLeft
    } else if (!minNumber) {
      Proportionality(8, 0.8, 2);
      yellowRight = totalSize * 0.111;
      redRight = totalSize * ( 1 - Proportion);
      G = totalSize - redRight - yellowRight;
      V = totalSize / (maxNumber / Proportion) * testing
    } else if (!maxNumber) {
      Proportionality(8, 0.8, 3);
      yellowLeft = minNumber
    }

    var data = [
        {value: redLeft, color: '#DE2121', Proportion: redLeft},
        {value: yellowLeft, color: '#FFDE08', Proportion: yellowLeft},
        {value: G, color: '#BDD639', Proportion: G},
        {value: yellowRight, color: '#FFDE08', Proportion: yellowRight},
        {value: redRight, color: '#DE2121', Proportion: redRight}
      ],
      circlevalue = [
        {value: 6, color: circleColor(V), Proportion: V},
        {value: 4, color: '#fff', Proportion: V},
        {value: 2, color: circleColor(V), Proportion: V}
      ],
      textValue = [{'location': redLeft - 3, value: minNumber}, {'location': totalSize - redRight - 3, value: maxNumber}],
      o = 0,
      n = 0;

    //添加条形图
    svg.append("g")
      .attr("width", w)
      .attr("height", h)
      .selectAll('rect')
      .data(data)
      .enter().append("rect")
      .attr("fill", function (d) {
        return d.color;
      })
      .attr("width", function (d) {
        return d.Proportion
      })
      .attr("x", function (d) {
        return xValue(d)
      })
      .attr("y", 4)
      .attr("height", 4);


    //添加圆圈
    svg.selectAll("circle")
      .data(circlevalue)
      .enter().append("circle")
      .attr("fill", function (d) {
        return d.color
      })
      .attr("cx", function (d) {
        return d.Proportion
      })
      .attr("cy", 6)
      .attr("r", function (d) {
        return d.value
      });

    svg.selectAll('text')
      .data(textValue)
      .enter().append('text')
      .attr('x', function (d) {
        return d.location
      })
      .attr("class", "textvalue")
      .attr("y", h)
      .text(function (d) {
        return d.value
      });


    //计算rect所在位置
    function xValue(item) {
      if (o === 0 && n === 0) {
        n = Traversal(item);
        return o
      }
      o = n;
      n += Traversal(item);
      return o
    }

    function Traversal(item) {
      for (var i = 0; i < data.length; i++) {
        if (data[i].Proportion === item.Proportion) {
          return data[i === 0 ? 0 : i].Proportion;
        }
      }
    }

    //计算除红色外所占比例
    function Proportionality(value, sc, example) {
      Proportion = sc;
      for (var i = 0; i < value; i++) {
        if (example === 1 && (difference / Proportion) - ((difference / Proportion - difference) / 2 - minNumber) < testing) {
          Proportion -= 0.1
        } else if (example === 2 && difference / Proportion < testing) {
          Proportion -= 0.1
        } else {
          break
        }
      }
    }

    //圆圈的color
    function circleColor(value) {
      var v1 = data[0].Proportion,
        v2 = v1 + data[1].Proportion,
        v3 = v2 + data[2].Proportion,
        v4 = v3 + data[3].Proportion;

      if (value < v1) {
        return data[0].color
      }
      else if (v1 <= value && value <= v2) {
        return data[1].color
      }
      else if (v2 < value && value < v3) {
        return data[2].color
      }
      else if (v3 <= value && value <= v4) {
        return data[3].color
      }
      else if (value > v4) {
        return data[4].color
      }
    }

  };

});

