/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.descriptors

import _root_.java.util

import org.apache.flink.table.api.ValidationException
import org.apache.flink.table.plan.stats.{ColumnStats, TableStats}
import org.junit.Test

class StatisticsTest extends DescriptorTestBase {

  @Test
  def testStatistics(): Unit = {
    val desc = Statistics()
      .rowCount(1000L)
      .columnStats("a", ColumnStats(1L, 2L, 3.0, 4, 5, 6))
      .columnAvgLength("b", 42.0)
      .columnNullCount("a", 300)
    val expected = Seq(
      "statistics.version" -> "1",
      "statistics.row-count" -> "1000",
      "statistics.columns.0.name" -> "a",
      "statistics.columns.0.distinct-count" -> "1",
      "statistics.columns.0.null-count" -> "300",
      "statistics.columns.0.avg-length" -> "3.0",
      "statistics.columns.0.max-length" -> "4",
      "statistics.columns.0.max-value" -> "5",
      "statistics.columns.0.min-value" -> "6",
      "statistics.columns.1.name" -> "b",
      "statistics.columns.1.avg-length" -> "42.0"
    )
    verifyProperties(desc, expected)
  }

  @Test
  def testStatisticsTableStats(): Unit = {
    val map = new util.HashMap[String, ColumnStats]()
    map.put("a", ColumnStats(null, 2L, 3.0, null, 5, 6))
    val desc = Statistics()
      .tableStats(TableStats(32L, map))
    val expected = Seq(
      "statistics.version" -> "1",
      "statistics.row-count" -> "32",
      "statistics.columns.0.name" -> "a",
      "statistics.columns.0.null-count" -> "2",
      "statistics.columns.0.avg-length" -> "3.0",
      "statistics.columns.0.max-value" -> "5",
      "statistics.columns.0.min-value" -> "6"
    )
    verifyProperties(desc, expected)
  }

  @Test(expected = classOf[ValidationException])
  def testInvalidRowCount(): Unit = {
    verifyInvalidProperty("statistics.row-count", "abx")
  }

  @Test(expected = classOf[ValidationException])
  def testMissingName(): Unit = {
    verifyMissingProperty("statistics.columns.0.name")
  }

  override def descriptor(): Descriptor = {
    Statistics()
      .rowCount(1000L)
      .columnStats("a", ColumnStats(1L, 2L, 3.0, 4, 5, 6))
      .columnAvgLength("b", 42.0)
      .columnNullCount("a", 300)
  }

  override def validator(): DescriptorValidator = {
    new StatisticsValidator()
  }
}
