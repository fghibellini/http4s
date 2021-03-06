/*
 * Copyright 2019 http4s.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.http4s.laws.discipline

import cats.Eq
import cats.effect._
import cats.effect.implicits._
import cats.laws.IsEq
import cats.syntax.all._
import org.scalacheck.Arbitrary
import org.scalacheck.Shrink
import org.scalacheck.effect.PropF

import munit.CatsEffectAssertions._

trait LawAdapter {

  def booleanPropF[F[_]: Effect, A: Arbitrary: Shrink](
      propLabel: String,
      prop: => Boolean): (String, PropF[IO]) =
    propLabel -> PropF.boolean(prop)

  def isEqPropF[F[_]: Effect, A: Arbitrary: Shrink, B: Eq](
      propLabel: String,
      prop: A => IsEq[F[B]]): (String, PropF[IO]) =
    propLabel -> PropF
      .forAllF { (a: A) =>
        val isEq = prop(a)
        (isEq.lhs, isEq.rhs).mapN(_ === _).toIO.assert
      }
      .map(p => p.copy(labels = p.labels + propLabel))

}

object LawAdapter extends LawAdapter
