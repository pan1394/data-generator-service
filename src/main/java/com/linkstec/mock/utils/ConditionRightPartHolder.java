package com.linkstec.mock.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class ConditionRightPartHolder {

  private String right;

  private String value;

  private List<String> container = new ArrayList<>();

  private MasterMapperExecutor executor;

  public ConditionRightPartHolder(MasterMapperExecutor executor, String right) {
    this.executor = executor;
    this.right = StringUtils.trim(right);
    handle();
  }

  private void handle() {
    if (this.right.startsWith(MockConstants.STRING_SUB_QUERY)) {
      this.right = this.right.substring(MockConstants.STRING_SUB_QUERY.length());
      this.right = MockUtils.removeParent(this.right);
      if (this.right.toLowerCase().contains("from")) {
        // sub query select
        container.addAll(executor.executeCustomSql(right));
      } else {
        //single field
        container.addAll(executor.execute(right));
      }
    } else {
      this.right = MockUtils.removeParent(this.right);
      int tmp = this.right.indexOf(",");
      if (tmp == -1) {
        //a signle field
        container.add(this.right);
      } else {
        String[] array = this.right.split(",");
        container.addAll(Arrays.asList(array));
      }
    }
    Collections.shuffle(container, new Random());
    this.value = container.get(0);
  }

  public String get() {
    return this.value;
  }
}
